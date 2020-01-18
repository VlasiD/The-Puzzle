import acm.graphics.GImage;
import acm.graphics.GObject;
import acm.program.GraphicsProgram;
import java.awt.event.MouseEvent;
import java.util.Random;

/**
 * The BreakOut Game
 */
public class Puzzle extends GraphicsProgram implements Constants {

    /* The object which has been selected for dragging. */
    private GObject selectedObject = null;

    /* The mouse's last position. */
    private double lastX = 0, lastY = 0;

    /**
     * Main method of program
     */
    public void run() {
        createPlayField();
        addMouseListeners();
    }

    /**
     * Creates play field and add cells
     */
    private void createPlayField() {
        /* Size of each cell */
        double cellWidth = getWidth()/FIELD_DIMENSION;
        double cellHeight = getHeight()/FIELD_DIMENSION;
        /* X and Y coordinates of all cells on play field */
        double[] xCoordinates = getXCoordinates(cellWidth);
        double[] yCoordinates = getYCoordinates(cellHeight);

        createCells(xCoordinates, yCoordinates);
    }

    /**
     * Adds cells to the play field on given coordinates
     *
     * @param xCoordinates of cells in the row
     * @param yCoordinates of cells in the column
     */
    private void createCells(double[] xCoordinates, double[] yCoordinates) {
        int number = 1;
        /* Pass through the X and Y coordinates and add images in given positions */
        for(int i = 0; i < xCoordinates.length; i++) {
            for (int j = 0; j < yCoordinates.length; j++) {
                /* the last element do not add */
                if(i == xCoordinates.length - 1 && j == yCoordinates.length - 1) {
                    break;
                }
                String fileName = "assets/" + number + ".png";
                add(new GImage(fileName), xCoordinates[i], yCoordinates[j]);
                number++;
            }
        }
    }

    /**
     * Return array of coordinates on Y axe for all cells
     *
     * @param cellHeight the height of one cell
     * @return array of coordinates on Y axe for all cells
     */
    private double[] getYCoordinates(double cellHeight) {
        double[] yCoordinates = new double[FIELD_DIMENSION];
        /* defines the Y coordinates for cells */
        for(int i = 0; i < FIELD_DIMENSION; i++) {
            yCoordinates[i] = cellHeight * i;
        }
        shuffle(yCoordinates);
        return yCoordinates;
    }

    /**
     * Return array of coordinates on X axe for all cells
     *
     * @param cellWidth the width of one cell
     * @return array of coordinates on Y axe for all cells
     */
    private double[] getXCoordinates(double cellWidth) {
        double[] xCoordinates = new double[FIELD_DIMENSION];
        /* defines the X coordinates for cells */
        for(int i = 0; i < FIELD_DIMENSION; i++) {
            xCoordinates[i] = cellWidth * i;
        }
        shuffle(xCoordinates);
        return xCoordinates;
    }

    /**
     * Shuffles elements of input array of random order
     *
     * @param array to shuffle
     */
    private void shuffle(double[] array) {
        Random rnd = new Random();
        /* pass through the array elements and swaps it */
        for (int i = 1; i < array.length; i++) {
            int j = rnd.nextInt(i);
            double temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }


    /**
     * Selects the object under the mouse cursor when the mouse is pressed.
     * If nothing is found, that's okay - we'll set selectedObject to null.
     */
    public void mousePressed(MouseEvent e) {
        selectedObject = getElementAt(e.getX(), e.getY());
        lastX = e.getX();
        lastY = e.getY();
    }

    /**
     * Repositions the dragged object to the mouse's location when the mouse
     * is moved.
     */
    public void mouseDragged(MouseEvent e) {
        /* If there is something to drag at all, go move it. */
        if (selectedObject != null) {
            double dx = e.getX() - lastX;
            double dy = e.getY() - lastY;
            movingLogic(dx, dy);
            onWindowControl();
            lastX = e.getX();
            lastY = e.getY();
        }
    }

    /**
     * Defines logic for rectangles moving
     *
     * @param dx cursor position change on X axe
     * @param dy cursor position change on Y axe
     */
    private void movingLogic(double dx, double dy) {
        /* check for obstacles on the top if rect move to the top */
        if (getElementAt(selectedObject.getX() + selectedObject.getWidth()/2 - 1,
                selectedObject.getY() - 1) == null && dy < 0) {         // top
            selectedObject.move(0, dy);
        }
        /* check for obstacles on the bottom if rect move to the bottom */
        else if (getElementAt(selectedObject.getX() + selectedObject.getWidth()/2,
                selectedObject.getY() + selectedObject.getHeight() + 1) == null && dy > 0) {
            selectedObject.move(0, dy);
        }
        /* check for obstacles on the left side if rect move to the left */
        else if (getElementAt(selectedObject.getX() - 1,
                selectedObject.getY() + selectedObject.getHeight()/2) == null && dx < 0) {
            selectedObject.move(dx, 0);
        }
        /* check for obstacles on the right side if rect move to the right */
        else if (getElementAt(selectedObject.getX() + selectedObject.getWidth() + 1,
                selectedObject.getY() + selectedObject.getHeight()/2) == null && dx > 0) {
            selectedObject.move(dx, 0);
        }
    }


    /**
     * Holds extreme squares within a window
     */
    private void onWindowControl() {
        /* if rect is at the left edge of the window */
        if(selectedObject.getX() < 0) {
            selectedObject.setLocation(0, selectedObject.getY());
        }
        /* if rect is at the right edge of the window */
        else if(selectedObject.getX() + selectedObject.getWidth() > getWidth()) {
            selectedObject.setLocation(getWidth() - selectedObject.getWidth(), selectedObject.getY());
        }
        /* if rect is at the upper edge of the window */
        else if(selectedObject.getY() < 0) {
            selectedObject.setLocation(selectedObject.getX(), 0);
        }
        /* if rect is at the lower edge of the window */
        else if(selectedObject.getY() + selectedObject.getHeight() > getHeight()) {
            selectedObject.setLocation(selectedObject.getX(), getHeight() - selectedObject.getHeight());
        }
    }

    /**
     * Determines the position when you release the mouse and move selected cell there
     */
    public void mouseReleased(MouseEvent e) {

        if (selectedObject != null) {
            double cellWidth = getWidth()/FIELD_DIMENSION;
            double[] xCoordinates = getXCoordinates(cellWidth);
            double min = cellWidth;
            double nearestX = 0;
            double nearestY = 0;
            /* pass through the X coordinates and compare distances from selected object to nearest X coordinate */
            for (double coordinate : xCoordinates) {
                double difference = Math.abs(coordinate - selectedObject.getX());
                /* find min distance */
                if (difference < min) {
                    min = difference;
                    nearestX = coordinate;
                }
            }

            double cellHeight = getHeight()/FIELD_DIMENSION;
            double[] heightArray = getYCoordinates(cellHeight);
            min = cellHeight;
            /* pass through the Y coordinates and compare distances from selected object to nearest Y coordinate */
            for (double coordinate : heightArray) {
                double difference = Math.abs(coordinate - selectedObject.getY());
                /* find min distance */
                if (difference < min) {
                    min = difference;
                    nearestY = coordinate;
                }
            }
            selectedObject.setLocation(nearestX, nearestY);
        }
    }
}
