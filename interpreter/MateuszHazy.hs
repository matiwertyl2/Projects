{-# LANGUAGE Safe #-}
module MateuszHazy (typecheck, eval) where

import AST
import DataTypes
import qualified Data.Map as Map
import Data.Maybe

-- sprawdzanie typow

typecheck :: [FunctionDef p] -> [Var] -> Expr p -> TypeCheckResult p
typecheck  functions vars e =
  let fcheck= check_functions functions (init_env_functions (Map.fromList []) functions) in
    if is_ok fcheck then get_type_answer ( infer_type (init_env vars functions) e) e
    else get_funerror_answer fcheck

data Typ p
  = TypInt
  | TypBool
  | TypError p String
  | TypErr
  | TypUnit
  | TypPair (Typ p) (Typ p)
  | TypList (Typ p)
  | TypFun (Typ p) (Typ p)
  deriving (Eq, Show)

data Fcheck p
  = Oks
  | FError (Typ p)

is_ok Oks = True
is_ok _ = False

get_funerror_answer (FError (TypError p s)) = Error p s

get_type_answer (TypError p err) e= Error p err
get_type_answer TypInt e= Ok
get_type_answer _ e= Error (getData e) "Wrong Output"

init_env vars functions = init_env_functions (init_env_vars vars) functions

init_env_vars vars = Map.fromList(vars_with_type vars)

init_env_functions acc []= acc
init_env_functions acc (f:fs) =
  let newacc = Map.insert (funcName f) (create_func_type f) acc in
    init_env_functions newacc fs

create_func_type f = TypFun (convert_type ( funcArgType f) ) (convert_type (funcResType f) )

vars_with_type []= []
vars_with_type (x:xs)= ((x, TypInt):vars_with_type(xs))

check_functions [] env = Oks
check_functions (f:fs) env = let fcheck= check_function f env in
  if is_ok fcheck then check_functions fs env
  else fcheck

check_function f env=
  let argtype= funcArgType f
      restype = funcResType f
      e = funcBody f
      var = funcArg f in
      let newenv= Map.insert var (convert_type argtype) env in
        let etype = infer_type newenv e in
          if is_error etype then FError etype
          else
            if same_type (convert_type restype) etype then Oks
            else FError ( TypError (funcPos f) "Wrong function type")

infer_type env (ENum _ _) = TypInt
infer_type env (EBool _ _) = TypBool
infer_type env (EVar p x) =
    let xtype= Map.lookup x env in
      if isNothing xtype then TypError p "Undefined Variable"
      else fromJust xtype
infer_type env (EUnit p)= TypUnit

infer_type env (EPair p e1 e2)=
  let t1= infer_type env e1
      t2= infer_type env e2 in
      if (is_error t1) then t1
      else if (is_error t2) then t2
      else (TypPair t1 t2)

infer_type env (EFst p e)=
  let t= infer_type env e in
    if is_error t then t
    else if (is_pair t) then get_type_first t
    else TypError p "Not a pair"

infer_type env (ESnd p e) =
  let t = infer_type env e in
    if is_error t then t
    else if (is_pair t) then get_type_second t
    else TypError p "Not a pair"

infer_type env (ENil p t) =  let  typ= (convert_type t) in
  if is_list typ then typ
  else TypError p "types dont match"

infer_type env (ECons p e1 e2) =
  let t1= infer_type env e1
      t2= infer_type env e2 in
      if is_error t1 then t1
      else if not (is_list t2) then TypError p "Not a List"
      else if same_type t1 (get_type_list t2) then (TypList t1)
      else (TypError p ("Types dont match"))

infer_type env (EMatchL p e nil cons) =
  let t0= infer_type env e in
    if not( is_list t0) then TypError p "Not a List"
    else
      let t1 = nilClause_type env  nil
          t2 = consClause_type env cons (get_type_list t0) in
          if is_error t1 then t1
          else if is_error t2 then t2
          else if (same_type t1 t2) then t1
          else TypError p "Types Dont match"

infer_type env (EApp p e1 e2) =
  let te1= infer_type env e1 in
      if is_error te1 then te1
      else
        let te2 = infer_type env e2 in
          if is_error te2 then te2
          else if not (is_function te1) then TypError p "Types dont match"
          else
            let fun_in= get_fun_in_type te1
                fun_out = get_fun_out_type te1 in
                  if same_type fun_in te2 then fun_out
                  else TypError p "Types dont match"

infer_type env (EFn p var typ e) =
    let t= convert_type typ
        newenv= Map.insert var t env in
          let te = infer_type newenv e in
            if is_error te then te
            else (TypFun t te)

infer_type env (EUnary p UNot x ) =
  let xt= infer_type env  x in
    if is_tbool xt then TypBool
    else TypError p "Types dont match"
infer_type env (EUnary p UNeg x) =
  let xt= infer_type env  x in
    if is_tint xt then TypInt
    else TypError p "Types dont match"

infer_type env  (EBinary p op e1 e2) =
  let e1t= infer_type env  e1
      e2t= infer_type env  e2 in
      if is_error e1t then e1t
      else if is_error e2t then e2t
      else
        if same_type (actual_type op e1t e2t) (expected_type op) then (expected_type op)
        else  TypError p "Types dont match"

infer_type env  (ELet p var e1 e2) =
  let e1t= infer_type env  e1 in
    if is_error e1t then e1t
    else infer_type (Map.insert var e1t env) e2

infer_type env (EIf p e1 e2 e3) =
  let e1t= infer_type env e1 in
    if is_tbool e1t then
      let e2t= infer_type env e2
          e3t= infer_type env e3 in
          if is_error e2t then e2t
          else if is_error e3t then e3t
          else
            if same_type e2t e3t then e2t
            else TypError p "Types dont match"
    else TypError p "Types dont match"

nilClause_type env e= infer_type env e

consClause_type env (x, y , e) typ =
  let envnew = Map.insert y (TypList typ) (Map.insert x  typ env) in
    infer_type envnew e

convert_type TInt= TypInt
convert_type TBool = TypBool
convert_type TUnit = TypUnit
convert_type (TPair a b)= TypPair (convert_type a) (convert_type b)
convert_type (TList t) = TypList (convert_type t)
convert_type (TArrow a b) = TypFun (convert_type a) (convert_type b)

is_error (TypError p s)= True
is_error _ =False

is_tbool TypBool =True
is_tbool _= False

is_tint TypInt = True
is_tint _=  False

is_pair (TypPair t1 t2)= True
is_pair _=False

is_list (TypList _) = True
is_list _= False

is_function (TypFun _ _)=True
is_function _ = False

get_type_first (TypPair t1 t2) = t1
get_type_second (TypPair t1 t2) =t2

get_type_list (TypList t)=t

get_fun_in_type (TypFun t1 t2)=t1
get_fun_out_type (TypFun t1 t2)=t2

same_type TypInt TypInt = True
same_type TypBool TypBool =True
same_type TypUnit TypUnit = True
same_type (TypList t1) (TypList t2) =
    if same_type t1 t2 then True
    else False
same_type (TypPair a1 a2) (TypPair b1 b2) =
  if (same_type a1 b1) && (same_type a2 b2) then True
  else False
same_type (TypFun a1 a2) (TypFun b1 b2) =
  if (same_type a1 b1) && (same_type a2 b2) then True
  else False
same_type _ _=False

expected_type op
  | arithmetic_operator op = TypInt
  | compare_operator op = TypBool
  | bool_operator op = TypBool

actual_type op t1 t2
  | arithmetic_operator op =
      if is_tint t1 && is_tint t2 then TypInt else TypErr
  | compare_operator op =
      if is_tint t1 && is_tint t2 then TypBool else TypErr
  | bool_operator op =
      if is_tbool t1 && is_tbool t2 then TypBool else TypErr

arithmetic_operator op = elem op [BAdd, BSub, BMul, BDiv, BMod]
compare_operator op = elem op [BEq, BNeq, BGt, BLt, BLe, BGe]
bool_operator op = elem op [BAnd, BOr]

-- wyliczanie wartosci

type Environment p= Map.Map Var (Val p)

data Val p
  = VInt Integer
  | VBool Bool
  | VUnit
  | VList [Val p]
  | VPair ((Val p), (Val p))
  | VFun [(FunctionDef p)] (FunctionDef p)-- srodowisko (F) nazwa funkcji argument i cialo
  | VFn (Environment p) Var (Expr p) -- srodowisko argument i cialo
  | VError deriving (Eq, Show)

is_verror VError = True
is_verror _ = False

eval :: [FunctionDef p] -> [(Var,Integer)] -> Expr p -> EvalResult
eval functions input e = get_answer(get_value (init_env_val functions input) e)

init_env_val functions input = init_env_val_vars (init_env_fun_val [] functions functions ) input

init_env_fun_val acc [] functions= Map.fromList acc
init_env_fun_val acc (f:fs) functions=
  let x= ((funcName f), (VFun functions f)) in
    init_env_fun_val (x:acc) fs functions

init_env_val_vars acc []= acc
init_env_val_vars acc ((var, value):vs) =
  let newacc = Map.insert var (VInt value) acc in
    init_env_val_vars newacc vs

get_answer VError = RuntimeError
get_answer (VInt x) = (Value x)

get_value env (ENum _ x)= VInt x
get_value env (EBool _ x)=VBool x
get_value env (EVar _ x) = if (isNothing value) then VError else fromJust value where value= Map.lookup x env
get_value env (EPair _ e1 e2) =
    let f=get_value env e1
        s=get_value env e2 in
        if is_verror f || is_verror s then VError
        else VPair (f, s)

get_value env (EUnit _) = VUnit

get_value env (EFst _ e) = let v= get_value env e in
  if is_verror v then VError
    else get_first v
get_value env (ESnd _ e) = let  v= get_value env e in
  if is_verror v then VError
    else get_second v

get_value env (EUnary _ op e)= let ve= get_value env e in
  if is_verror ve then VError
  else unary_operation ve

get_value env (EBinary _ op e1 e2) =
  let ve1 = get_value env e1 in
    let ve2= get_value env e2 in
      if (is_verror ve1 || is_verror ve2) then VError
      else binary_operation op ve1 ve2

get_value env (EIf _ e1 e2 e3)=
  let ve1= get_value env  e1 in
    if is_verror ve1 then VError
    else if_operation env ve1 e2 e3

get_value env (ELet _ var e1 e2)=
  let ve1= get_value env e1 in
    if is_verror ve1 then VError
    else get_value (Map.insert var ve1 env) e2

get_value env (ENil _ _) = VList []

get_value env (ECons _ e1 e2) =
    let v1= get_value env e1 in
      let v2= get_value env e2 in
        if (is_verror v1) || (is_verror v2) then VError
          else VList (v1:(get_value_list v2))

get_value env (EMatchL _ e nil cons) =
  let v= get_value env e in
    if is_verror v then VError
      else
         if empty_list v then nilClause_case env nil
           else consClause_case env cons v

get_value env (EFn p var t e) = VFn env var e

get_value env (EApp p e1 e2) =
  let ve1= get_value env e1 in
    if is_func ve1 then
      let ve2 = get_value env e2 in
          if is_verror ve2 then VError
          else
            let envprim = get_pf ve1
                body = get_function_body ve1
                var = get_function_arg ve1 in
                  let env_extended = Map.insert var ve2 envprim in
                    get_value env_extended body
    else if is_fn ve1 then
      let ve2 = get_value env e2 in
          if is_verror ve2 then VError
          else
            let envprim = get_fn_env ve1
                body = get_fn_body ve1
                var = get_fn_arg ve1 in
                  let env_extended = Map.insert var ve2 envprim in
                    get_value env_extended body
    else VError

is_fn (VFn _ _ _) = True
is_fn _ = False

get_fn_body (VFn _ _ e)= e
get_fn_env (VFn env _ _) =env
get_fn_arg (VFn _ var _) = var


is_func (VFun _ _)= True
is_func _ = False

get_function_body (VFun _ f) = funcBody f

get_function_arg (VFun _ f) = funcArg f

get_pf (VFun func _) = init_env_fun_val [] func func

consClause_case env (x, y, e) (VList (v0:vs)) =
  get_value newenv e where
    newenv= Map.insert y (VList vs) (Map.insert x (v0) env)

nilClause_case env e = get_value env e

empty_list (VList []) = True
empty_list _ = False

get_value_list (VList x)=x

get_first (VPair (v1, _))= v1
get_second (VPair (_, v2))=v2

unary_operation (VBool x)=  VBool (not x)
unary_operation (VInt x)= VInt (-x)

binary_operation BAnd (VBool a) (VBool b) = VBool (a && b)
binary_operation BOr (VBool a) (VBool b) = VBool (a || b)

binary_operation BEq (VInt a) (VInt b) = VBool (a==b)
binary_operation BNeq (VInt a) (VInt b) = VBool (a/=b)
binary_operation BLt (VInt a) (VInt b) = VBool (a<b)
binary_operation BGt (VInt a) (VInt b) = VBool (a>b)
binary_operation BLe (VInt a) (VInt b) = VBool (a<=b)
binary_operation BGe (VInt a) (VInt b) = VBool (a>=b)

binary_operation BAdd (VInt a) (VInt b) = VInt (a+b)
binary_operation BSub (VInt a) (VInt b) = VInt (a-b)
binary_operation BMul (VInt a) (VInt b) = VInt (a*b)
binary_operation BDiv (VInt a) (VInt b)
  | b==0 = VError
  | otherwise = VInt (div a b)
binary_operation BMod (VInt a) (VInt b)
  | b==0 = VError
  | otherwise =VInt (mod a b)

if_operation env (VBool condition) e1 e2 =
  if condition==True then get_value env e1
  else get_value env e2
