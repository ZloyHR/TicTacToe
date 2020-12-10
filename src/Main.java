import acm.graphics.GImage;
import acm.io.IODialog;
import acm.program.GraphicsProgram;
import acm.util.RandomGenerator;

import java.awt.*;
import java.awt.event.MouseEvent;

public class Main extends GraphicsProgram {
    Figure current = Figure.CROSS;

    Figure player;
    Figure computer;
    private int windowHeight = 760;
    private int windowWidth = 720;

    private IODialog dialog = new IODialog();

    private int usedCells = 0;

    private Point usable = new Point(60,60);
    private Point cellSize = new Point(180,180);

    private int lineWidth = 20;

    @Override
    public void run(){
        this.setSize(windowWidth,windowHeight);
        addMouseListeners();
        dialog.println("This is Harasho");
        game();
    }

    public void game(){
        removeAll();
        setBackground(Color.black);
        drawGird();
        player = getPlayer();
        computer = player.nextFigure();
        if(player == Figure.CIRCLE)computerMove();
    }

    public void reload(){
        c00 = c01 = c02 = c10 = c11 = c12 = c20 = c21 = c22 = null;
        current = Figure.CROSS;
        usedCells = 0;
        game();
    }

    public void endGame(){
        Figure winner = checkWin();
        String text;
        if(winner == null)text = "DRAW";
        else text = winner + " wins";
        text += "\nDo you want to continue?";
        boolean ans = dialog.readBoolean(text,"Yes","No");
        if(ans){
            reload();
        }else{
            exit();
        }
    }

    public Figure getPlayer(){
        boolean type = dialog.readBoolean("Who will you be?","X","O");
        return type ? Figure.CROSS : Figure.CIRCLE;
    }

    public void computerMove(){

        if(check(computer) || check(player))return;

        int it = RandomGenerator.getInstance().nextInt(9 - usedCells) + 1;
        for(int i = 0;i < 3; ++i){
            for(int j = 0;j < 3; ++j){
                if(getFigure(new Point(j,i)) == null)--it;
                if(it == 0){
                    createFigure(new Point(j,i));
                    return;
                }
            }
        }
    }

    public boolean check(Figure figure){
        if(getPos(getLine(Line.LEFT_DIAGONAL,new Point(0,0)),figure) == 2){
            for(int i = 0;i < 3; ++i){
                if(getFigure(new Point(i,i)) == null){
                    createFigure(new Point(i,i));
                    return true;
                }
            }
        }

        if(getPos(getLine(Line.RIGHT_DIAGONAL,new Point(0,0)),figure) == 2){
            for(int i = 0;i < 3; ++i){
                if(getFigure(new Point(i,2 - i)) == null){
                    createFigure(new Point(i,2 - i));
                    return true;
                }
            }
        }

        for(int i = 0;i < 3; ++i){
            if(getPos(getLine(Line.HORIZONTAL,new Point(i,0)),figure) == 2){
                for(int j = 0;j < 3; ++j){
                    if(getFigure(new Point(i,j)) == null){
                        createFigure(new Point(i,j));
                        return true;
                    }
                }
            }
            if(getPos(getLine(Line.VERTICAL,new Point(0,i)),figure) == 2){
                for(int j = 0;j < 3; ++j){
                    if(getFigure(new Point(j,i)) == null){
                        createFigure(new Point(j,i));
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        Point cursor = mouseEvent.getPoint();
        Point cell = getCell(cursor);

        if(cell.x != -1 && getFigure(cell) == null){
            createFigure(cell);
        }

        Figure winner = checkWin();
        if(winner != null){
            endGame();
        }
        if(usedCells == 9){
            endGame();
        }
        if(!current.equals(player))computerMove();
        winner = checkWin();
        if(winner != null){
            endGame();
        }
        if(usedCells == 9){
            endGame();
        }
    }

    public void createFigure(Point cell){
        drawFigure(current,cell);
        setFigure(current,cell);
        current = current.nextFigure();
        usedCells++;
    }

    private Point getCell(Point cursor){
        for(int i = 0;i < 3; ++i){
            for(int j = 0;j < 3; ++j){
                Point lu = new Point(usable.x,usable.y);
                lu.x += (cellSize.x + lineWidth) * i;
                lu.y += (cellSize.y + lineWidth) * j;
                Point rd = new Point(lu.x,lu.y);
                rd.x += cellSize.x;
                rd.y += cellSize.y;
                if((lu.x <= cursor.x && cursor.x <= rd.x) &&
                        (lu.y <= cursor.y && cursor.y <= rd.y)){
                    return new Point(i,j);
                }

            }
        }
        return new Point(-1,-1);
    }

    private int getPos(Point p,Figure figure){
        return figure == Figure.CROSS ? p.x : p.y;
    }

    public void drawFigure(Figure figure, Point cell){
        GImage img = new GImage("img/" + figure + ".PNG");
        Point coord = new Point();
        coord.x = usable.x + cell.x * (cellSize.x + lineWidth);
        coord.y = usable.y + cell.y * (cellSize.y + lineWidth);
        img.move(coord.x,coord.y);
        add(img);
    }

    public void drawGird(){
        GImage img = new GImage("img/GRID.PNG");
        add(img);
    }

    public void drawLine(Line line, Point cell){
        GImage img = new GImage("img/" + line + "_LINE.PNG");
        img.move(cell.x * (cellSize.x + lineWidth),cell.y * (cellSize.y + lineWidth));
        add(img);
    }

    public Point getLine(Line line,Point cell){
        int cross = 0,circle = 0;
        if(line == Line.LEFT_DIAGONAL){
            for(int i = 0;i < 3; ++i){
                if(getFigure(new Point(i,i)) == Figure.CIRCLE)circle++;
                if(getFigure(new Point(i,i)) == Figure.CROSS)cross++;
            }
        }else if (line == Line.RIGHT_DIAGONAL){
            for(int i = 0;i < 3; ++i){
                if(getFigure(new Point(i,2 - i)) == Figure.CIRCLE)circle++;
                if(getFigure(new Point(i,2 - i)) == Figure.CROSS)cross++;
            }
        }else if (line == Line.HORIZONTAL){
            for(int i = 0;i < 3; ++i){
                if(getFigure(new Point(cell.x,i)) == Figure.CIRCLE)circle++;
                if(getFigure(new Point(cell.x,i)) == Figure.CROSS)cross++;
            }
        }else if(line == Line.VERTICAL){
            for(int i = 0;i < 3; ++i){
                if(getFigure(new Point(i,cell.y)) == Figure.CIRCLE)circle++;
                if(getFigure(new Point(i,cell.y)) == Figure.CROSS)cross++;
            }
        }
        return new Point(cross,circle);
    }

    public Figure checkWin(){
        Point res;
        res = getLine(Line.RIGHT_DIAGONAL,new Point(0,0));
        if(checkWin(res) != null){
            drawLine(Line.RIGHT_DIAGONAL,new Point(0,0));
            return checkWin(res) ;
        }
        res = getLine(Line.LEFT_DIAGONAL,new Point(0,0));
        if(checkWin(res) != null){
            drawLine(Line.LEFT_DIAGONAL,new Point(0,0));
            return checkWin(res) ;
        }
        for(int i = 0;i < 3; ++i){
            res = getLine(Line.HORIZONTAL,new Point(i,0));
            if(checkWin(res) != null){
                drawLine(Line.HORIZONTAL,new Point(i,0));
                return checkWin(res) ;
            }
            res = getLine(Line.VERTICAL,new Point(0,i));
            if(checkWin(res) != null){
                drawLine(Line.VERTICAL,new Point(0,i));
                return checkWin(res) ;
            }
        }
        return null;
    }

    public Figure checkWin(Point x){
        if(x.x == 3)return Figure.CROSS;
        if(x.y == 3)return Figure.CIRCLE;
        return null;
    }

    public Figure getFigure(Point cell){
        if(cell.x == 0 && cell.y == 0)return c00;
        if(cell.x == 0 && cell.y == 1)return c01;
        if(cell.x == 0 && cell.y == 2)return c02;
        if(cell.x == 1 && cell.y == 0)return c10;
        if(cell.x == 1 && cell.y == 1)return c11;
        if(cell.x == 1 && cell.y == 2)return c12;
        if(cell.x == 2 && cell.y == 0)return c20;
        if(cell.x == 2 && cell.y == 1)return c21;
        if(cell.x == 2 && cell.y == 2)return c22;
        return null;
    }

    public void setFigure(Figure figure,Point cell){
        if(cell.x == 0 && cell.y == 0)c00 = figure;
        if(cell.x == 0 && cell.y == 1)c01 = figure;
        if(cell.x == 0 && cell.y == 2)c02 = figure;
        if(cell.x == 1 && cell.y == 0)c10 = figure;
        if(cell.x == 1 && cell.y == 1)c11 = figure;
        if(cell.x == 1 && cell.y == 2)c12 = figure;
        if(cell.x == 2 && cell.y == 0)c20 = figure;
        if(cell.x == 2 && cell.y == 1)c21 = figure;
        if(cell.x == 2 && cell.y == 2)c22 = figure;
    }


    private Figure c00 = null,c01 = null,c02 = null,
                   c10 = null,c11 = null,c12 = null,
                   c20 = null,c21 = null,c22 = null;
}