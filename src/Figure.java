public enum Figure {
    CIRCLE,CROSS;

    public Figure nextFigure(){
        return this == CIRCLE ? CROSS : CIRCLE;
    }

    public char getChar(){
        return this == CIRCLE ? 'o' : 'x';
    }
}
