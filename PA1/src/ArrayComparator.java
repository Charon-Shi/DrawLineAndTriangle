import java.util.Comparator;

class ArrayComparator implements Comparator<Point2D> {
	    public int compare(Point2D p1, Point2D p2) {
	        if(p1.y > p2.y)
	        	return 1;
	        else 
	        	return -1;
	    }
	}