//****************************************************************************
// SketchBase.  
//****************************************************************************
// Comments : 
//   Subroutines to manage and draw points, lines an triangles
//
// History :
//   Aug 2014 Created by Jianming Zhang (jimmie33@gmail.com) based on code by
//   Stan Sclaroff (from CS480 '06 poly.c)

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

public class SketchBase 
{	
	public SketchBase()
	{
		// deliberately left blank
	}
	
	// draw a point
	public static void drawPoint(BufferedImage buff, Point2D p)
	{
		buff.setRGB(p.x, buff.getHeight()-p.y-1, p.c.getBRGUint8());
	}
	
	//next point for line with no slope
	private static PointData NoSlopeNextPoint(Point2D p1, Point2D p2, PointData Current) {
		int iy;
		float icR, icG, icB;
		PointData step = Current;
		
		iy = (p2.y > p1.y) ? 1 : -1;
			
		//set color and get next point	
		if(Current.Smooth) {
			icR = p1.c.r + (p2.c.r - p1.c.r) * (float)(step.Point.y - p1.y)/(p2.y - p1.y);
			icG = p1.c.g + (p2.c.g - p1.c.g) * (float)(step.Point.y - p1.y)/(p2.y - p1.y);
			icB = p1.c.b + (p2.c.b - p1.c.b) * (float)(step.Point.y - p1.y)/(p2.y - p1.y);
			
			step.Point.c.r = icR;
			step.Point.c.g = icG;
			step.Point.c.b = icB;	
			step.Point.y += iy;
		}
		else {
			step.Point.c.r = Current.Color.r;
			step.Point.c.g = Current.Color.g;
			step.Point.c.b = Current.Color.b;	
			step.Point.y += iy;
		}
		
		return step;	
	}

	//next point for line with slope <= 1
	private static PointData LittleSlopeNextPoint(Point2D p1, Point2D p2, PointData Current) {
		int ix, iy;
		float icR, icG, icB;
		PointData step = Current;
		
		//calculate general info
		int dx = Math.abs(p2.x - p1.x);
		int dy = Math.abs(p2.y - p1.y);

		ix = (p2.x > p1.x) ? 1 : -1;
		iy = (p2.y > p1.y) ? 1 : -1;	
			
		//calculate decision parameter, two*delta_Y and two*delta_Y minus two_delta_X
		int YY = 2*dy;
		int YYXX = 2*(dy -dx);
			
		//get Next Point	
		if(step.DecisionParameter < 0) 
			step.DecisionParameter += YY; 
		else {
			step.Point.y += iy;
			step.DecisionParameter += YYXX;
		}
	
		// set color and get next point
		if(Current.Smooth) {
			icR = p1.c.r + (p2.c.r - p1.c.r) * (float)(step.Point.x - p1.x)/(p2.x - p1.x);
			icG = p1.c.g + (p2.c.g - p1.c.g) * (float)(step.Point.x - p1.x)/(p2.x - p1.x);
			icB = p1.c.b + (p2.c.b - p1.c.b) * (float)(step.Point.x - p1.x)/(p2.x - p1.x);
			
			step.Point.c.r = icR;
			step.Point.c.g = icG;
			step.Point.c.b = icB;	
			step.Point.x += ix;
		}
		else {
			step.Point.c.r = Current.Color.r;
			step.Point.c.g = Current.Color.g;
			step.Point.c.b = Current.Color.b;	
			step.Point.x += ix;
		}

		return step;
	}

	//next point for line with slope > 1
	private static PointData LargeSlopeNextPoint(Point2D p1, Point2D p2, PointData Current) {
		int ix, iy;
		float icR, icG, icB;
		PointData step = Current;
		
		//calculate general info
		int dx = Math.abs(p2.x - p1.x);
		int dy = Math.abs(p2.y - p1.y);

		ix = (p2.x > p1.x) ? 1 : -1;
		iy = (p2.y > p1.y) ? 1 : -1;
		
		//calculate decision parameter, two*delta_Y and two*delta_Y minus two_delta_X
		int XX = 2*dx;
		int XXYY = 2*(dx -dy);
		
		//get next point 	
		if(step.DecisionParameter < 0) 
			step.DecisionParameter += XX; 
		else {
			step.Point.x += ix;
			step.DecisionParameter += XXYY;
		}
		//set color
		if(Current.Smooth) {
			icR = p1.c.r + (p2.c.r - p1.c.r) * (float)(step.Point.y - p1.y)/(p2.y - p1.y);
			icG = p1.c.g + (p2.c.g - p1.c.g) * (float)(step.Point.y - p1.y)/(p2.y - p1.y);
			icB = p1.c.b + (p2.c.b - p1.c.b) * (float)(step.Point.y - p1.y)/(p2.y - p1.y);	
			
			step.Point.c.r = icR;
			step.Point.c.g = icG;
			step.Point.c.b = icB;
			step.Point.y += iy;
		}
		else {
			step.Point.c.r = Current.Color.r;
			step.Point.c.g = Current.Color.g;
			step.Point.c.b = Current.Color.b;
			step.Point.y += iy;
		}
			
		return step;
	}
	
	//get Next point
	@SuppressWarnings("unused")
	private static PointData NextPoint(Point2D p1, Point2D p2, PointData Current) {
		if(Current.Slope == -1)
			return NoSlopeNextPoint(p1, p2, Current);
		else if(Current.Slope < 1)
			return LittleSlopeNextPoint(p1, p2, Current);
		else 
			return LargeSlopeNextPoint(p1, p2, Current);
	}
	
	// draw a line segment
	public static void drawLine(BufferedImage buff, Point2D p1, Point2D p2)
	{
		//System.out.println("x1:" + p1.x +"\ty1:" + p1.y + "\tx2:" + p2.x +"\ty2:" + p2.y);
		PointData step = new PointData(p1);
		
		//calculate general info
		int dx = Math.abs(p2.x - p1.x);
		int dy = Math.abs(p2.y - p1.y);
		
		float _Slope = -1;
		if(dx != 0) {
			_Slope = (float)dy/dx;
			step.Slope = _Slope;
		}
		
		// draw lines with 0 <= slope <= 1
		if(_Slope <= 1.0 && _Slope >= 0) {
			step.DecisionParameter = 2*dy - dx;
				
			while(step.Point.x != p2.x) {
				drawPoint(buff, step.Point);
				step = NextPoint(p1, p2, step);
			}
			drawPoint(buff, p2);
		} 
		else { //draw line for slope > 1 or slope = -1(no slope actually)	
			step.DecisionParameter = 2*dx - dy;
			
			while(step.Point.y != p2.y) {
				drawPoint(buff, step.Point);	
				step = NextPoint(p1, p2, step);
			}
			drawPoint(buff, p2);
		}
	}
	
	// draw random triangle
	public static void drawTriangle(BufferedImage buff, Point2D p1, Point2D p2, Point2D p3, boolean do_smooth)
	{	
		Point2D high, middle, low;
		ArrayList<Point2D> List = new ArrayList<Point2D>();
		
		//three points are one single line or one single point
		if((p1.y == p2.y && p1.y == p3.y) || (p1.x == p2.x && p1.x == p3.x)) 
			return;
		
		//sort Point2D p1/p2/p3 from high to low position
		List.add(p1);
		List.add(p2);
		List.add(p3);
		Collections.sort(List,new ArrayComparator());
		
		high = new Point2D(List.get(0));
		middle = new Point2D(List.get(1));
		low = new Point2D(List.get(2));
		
		drawFlatBottomTriangle(buff, high, middle, low, do_smooth, p1.c);
		if(middle.y != low.y)
			drawFlatTopTriangle(buff, middle, high, low, do_smooth, p1.c);

	}
	
	//draw flat bottom triangle from top to bottom, p1 is top point
	@SuppressWarnings("unused")
	private static void drawFlatBottomTriangle(BufferedImage buff, Point2D p1, Point2D p2, Point2D p3, boolean do_smooth, ColorType Color)
	{	
		int Increment = p1.y;
		int Boundry = (p1.y > p2.y) ? p1.y : p2.y;
		PointData begin = new PointData(do_smooth, Color, p1);
		PointData end = new PointData(do_smooth, Color, p1);
		
		//calculate left edge slope and right edge slope
		int dx1 = Math.abs(p2.x - p1.x);
		int dy1 = Math.abs(p2.y - p1.y);
		
		float LeftSlope = -1;
		if(dx1 != 0){
			LeftSlope = (float)dy1/dx1;
			begin.Slope = LeftSlope;
		}
		
		int dx2 = Math.abs(p3.x - p1.x);
		int dy2 = Math.abs(p3.y - p1.y);
		float RightSlope = -1;
		
		if(dx2 != 0) {
			RightSlope= (float)dy2/dx2;
			end.Slope = RightSlope;
		}
		
		if(LeftSlope <= 1.0) 
			begin.DecisionParameter = 2*dy1 - dx1;
		else 
			begin.DecisionParameter = 2*dx1 - dy1;
			
		if(RightSlope <= 1.0) 
			end.DecisionParameter = 2*dy2 - dx2;
		else
			end.DecisionParameter = 2*dx2 - dy2;
		
		while(Increment != Boundry) { // scan-line	
			do {
				drawPoint(buff, begin.Point);
				begin = NextPoint(p1, p2, begin);
			}while(begin.Point.y != Increment+1);

			do {
				drawPoint(buff, end.Point);
				end = NextPoint(p1, p3, end);
			}while(end.Point.y != Increment+1);
			
			SketchBase.drawLine(buff, begin.Point, end.Point);
			System.out.println("begin:(" + begin.Point.x + ", " +begin.Point.y + ")\t\tend:(" + end.Point.x +", " + end.Point.y + ")");
			Increment++;
		}
	}

	//draw flat top triangle from bottom to top, p3 is bottom point
	@SuppressWarnings("unused")
	private static void drawFlatTopTriangle(BufferedImage buff, Point2D p1, Point2D p2, Point2D p3, boolean do_smooth, ColorType Color)
	{
		int Increment = p3.y;
		int Boundry = (p1.y > p2.y) ? p1.y : p2.y;
		PointData begin = new PointData(do_smooth, Color, p3);
		PointData end = new PointData(do_smooth, Color, p3);
		
		//calculate left edge slope and right edge slope
		int dx1 = Math.abs(p3.x - p1.x);
		int dy1 = Math.abs(p3.y - p1.y);
		float LeftSlope = -1;
		if(dx1 != 0) {
			LeftSlope = (float)dy1/dx1;
			begin.Slope = LeftSlope;
		}
		
		int dx2 = Math.abs(p3.x - p2.x);
		int dy2 = Math.abs(p3.y - p2.y);
		float RightSlope = -1;
		if(dx2 != 0) {
			RightSlope = (float)dy2/dx2;
			end.Slope = RightSlope;
		}
		
		if(LeftSlope <= 1.0) 
			begin.DecisionParameter = 2*dy1 - dx1;
		else 
			begin.DecisionParameter = 2*dx1 - dy1;
			
		if(RightSlope <= 1.0) 
			end.DecisionParameter = 2*dy2 - dx2;
		else
			end.DecisionParameter = 2*dx2 - dy2;
		
		while(Increment != Boundry) { // scan-line	
			do {
				drawPoint(buff, begin.Point);
				begin = NextPoint(p3, p1, begin);
			}while(begin.Point.y != Increment-1);

			do {
				drawPoint(buff, end.Point);
				end = NextPoint(p3, p2, end);
			}while(end.Point.y != Increment-1);
			
			SketchBase.drawLine(buff, begin.Point, end.Point);
			System.out.println("begin:(" + begin.Point.x + ", " +begin.Point.y + ")\t\tend:(" + end.Point.x +", " + end.Point.y + ")");
			Increment--;
		}
	}
	
	/////////////////////////////////////////////////
	// for texture mapping (Extra Credit for CS680)
	/////////////////////////////////////////////////
	public static void triangleTextureMap(BufferedImage buff, BufferedImage texture, Point2D p1, Point2D p2, Point2D p3)
	{
		// replace the following line with your implementation
		drawPoint(buff, p3);
	}
}
