const element = getCanvasElement();
const root = new createjs.Stage(element);
const stage = new createjs.Container();
root.addChild(stage);
const size = 360;
const animationSpeed = 250;
stage.x = (getCanvasWidth() - size) / 2;

var updates;

function animate(object, property, target) {
    if(object[property] == target) {
        return;
    }
    createjs.Tween.get(object).to({
        [property]: target,
    }, animationSpeed, createjs.Ease.getPowInOut(1));
}

//objects
function initObjects(n, final) {
    stage.removeAllChildren();
    const background = new createjs.Shape();
    background.graphics
        .beginFill('#ffffff')
        .drawRect(0, 0, size, size);
    stage.addChild(background);
    const blockSize = size / n;
    const colors = ['DeepSkyBlue', 'Tomato'];
    updates = final.trace.map((t, i) => {
        const col = colors[i];
        const head = new createjs.Shape();
        head.graphics.beginFill(col).drawRoundRect(-0.5 * blockSize, -0.5 * blockSize,
                                              1 * blockSize, 1 * blockSize, 0.2 * blockSize);
        console.log(t[0]);
        head.x = t[0].x;
        head.y = t[0].y;
        stage.addChild(head);
        const shapes = t.map((p, i) => {
            var left = -0.3, right = 0.3, top = -0.3, bottom = 0.3;
            left *= blockSize; right *= blockSize; top *= blockSize; bottom *= blockSize;
            const smallShape = new createjs.Shape();
            smallShape.graphics.beginFill(col).drawRect(left, top, right - left, bottom - top);
            smallShape.x = blockSize * (p.x + 0.5);
            smallShape.y = blockSize * (p.y + 0.5);
            smallShape.alpha = 0;
            stage.addChild(smallShape);

            if(i + 1 < t.length) {
                if(t[i+1].x < t[i].x) {
                    left -= 0.44 * blockSize;
                } else if(t[i+1].x > t[i].x) {
                    right += 0.44 * blockSize;
                } else if(t[i+1].y < t[i].y) {
                    top -= 0.44 * blockSize;
                } else if(t[i+1].y > t[i].y) {
                    bottom += 0.44 * blockSize;
                }
            }
            const shape = new createjs.Shape();
            shape.graphics.beginFill(col).drawRect(left, top, right - left, bottom - top);
            shape.x = blockSize * (p.x + 0.5);
            shape.y = blockSize * (p.y + 0.5);
            shape.alpha = 0;
            stage.addChild(shape);
            return {
                small: smallShape,
                full: shape
            };
        });
        var prevLen = -10;
        return (len) => {
            const p = shapes[len-1].full;
            if(prevLen + 1 == len) {
                shapes.forEach((s, i) => {
                    //animate(s, 'alpha', i < len ? 1 : 0);
                    s.full.alpha = i < len - 2 ? 1 : 0;
                    if(i == len - 2) {
                        s.small.alpha = 1;
                        setTimeout(() => {
                            s.small.alpha = 0;
                            s.full.alpha = 1;
                        }, animationSpeed / 2);
                    }
                });

                animate(head, 'x', p.x);
                animate(head, 'y', p.y);
            } else {
                shapes.forEach((s, i) => {
                    s.full.alpha = i < len - 1 ? 1 : 0;
                })
                head.x = p.x;
                head.y = p.y;
            }
            prevLen = len;
        };
    });
}

function initAnimation(history) {
    var n = history.updates[0].description.board.length;
    initObjects(n, history.updates[history.updates.length-1].description);
    setAnimationSpeed(animationSpeed);
}

function drawFrame(data) {
    updates.forEach((u, i) => u(data.description.trace[i].length));
}

root.update();
createjs.Ticker.setFPS(60);
createjs.Ticker.addEventListener("tick", root);