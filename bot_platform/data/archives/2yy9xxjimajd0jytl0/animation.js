const root = new createjs.Stage(getCanvasElement());
const stage = new createjs.Container();
root.addChild(stage);
const size = 200;
stage.x = (getCanvasWidth() - size * 3) / 2;

function makeLine(x1, y1, x2, y2) {
    const line = new createjs.Shape();
    line.graphics
        .setStrokeStyle(5)
        .beginStroke('gray')
        .moveTo(x1, y1)
        .lineTo(x2, y2);
    stage.addChild(line);
}

function centerReg(obj) {
    const bounds = obj.getBounds();
    obj.regX = bounds.width / 2;
    obj.regY = bounds.height / 2;
}

//grid
[size, size * 2].forEach(x => {
    makeLine(x, 0, x, size * 3);
    makeLine(0, x, size * 3, x);
});

circles = [];
crosses = [];

//objects
function initObjects(n) {
    for(var i = 0; i < n; i++) {
        circles[i] = [];
        crosses[i] = [];
        for(var j = 0; j < n; j++) {
            const cross = new createjs.Text('X', '20px Arial', 'black');
            cross.x = (i + 0.5)/n  * size;
            cross.y = (j + 0.5)/n * size;
            centerReg(cross);
            cross.alpha = 0;
            crosses[i][j] = cross;
            stage.addChild(cross);

            const circle = new createjs.Text('O', '20px Arial', 'black');
            circle.x = (i + 0.5)/n * size;
            circle.y = (j + 0.5)/n * size;
            centerReg(circle);
            circle.alpha = 0;
            circles[i][j] = circle;
            stage.addChild(circle);
        }
    }
}


function initAnimation(history) {
    var n = history.updates[0].description.board.length;
    initObjects(n);
    setAnimationSpeed(1000);
}

function animate(object, target) {
    if(object.alpha == target) {
        return;
    }
    createjs.Tween.get(object).to({
        alpha: target,
    }, 800, createjs.Ease.getPowInOut(4));
}

function drawFrame(data) {
    var board = data.description.board;
    for(var i = 0; i < board.length; i++) {
        for(var j = 0; j < board.length; j++) {
            const targetA = data.description.board[i][j] == 0 ? 1 : 0;
            const targetB = data.description.board[i][j] == 1 ? 1 : 0;
            animate(A[i][j], targetA);
            animate(B[i][j], targetB);
        }
    }
}

root.update();
createjs.Ticker.setFPS(60);
createjs.Ticker.addEventListener("tick", root);