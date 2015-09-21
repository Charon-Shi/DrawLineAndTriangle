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

	public static int getIntColor(Point2D p, String Type)
	{
		if(Type == "Red") 
			return p.c.getBRGUint8()>>16;
		else if(Type == "Green") 
			return (p.c.getBRGUint8()>>8 & 255);
		else if(Type == "Blue") 
			return (p.c.getBRGUint8() & 255);
		
		return -1;
	}
	
	// draw a point
	public static void drawPoint(BufferedImage buff, Point2D p)
	{
		buff.setRGB(p.x, buff.getHeight()-p.y-1, p.c.getBRGUint8());
	}
	
	//next point for line with no slope
	private static void NoSlopeNextPoint(Point2D p1, Point2D p2, PointData Current) {
		int iy, R, G, B, dred, dgreen, dblue;
		
		int dy = Math.abs(p2.y - p1.y);
		iy = (p2.y > p1.y) ? 1 : -1;
		
		dred = Math.abs(getIntColor(p2, "Red") - getIntColor(p1, "Red"));
		dgreen = Math.abs(getIntColor(p2, "Green") - getIntColor(p1, "Green"));
		dblue = Math.abs(getIntColor(p2, "Blue") - getIntColor(p1, "Blue"));
		
		//set color and get next point	
		if(Current.doSmoothShading) {
			R = dred/dy;
			Current.RedRemainder += (dred - R*dy);
			R = getIntColor(Current.Point, "Red") + (Current.RedIncrement)*R;			
			if(Current.RedRemainder*2 >= dy) {
				R += Current.RedIncrement;
				Current.RedRemainder -= dy;
			}

			G = dgreen/dy;
			Current.GreenRemainder += (dgreen - G*dy);
			G = getIntColor(Current.Point, "Green") + (Current.GreenIncrement)*G;			
			if(Current.GreenRemainder*2 >= dy) {
				G += Current.GreenIncrement;
				Current.GreenRemainder -= dy;
			}
			
			B = dblue/dy;
			Current.BlueRemainder += (dblue - B*dy);
			B = getIntColor(Current.Point, "Blue") + (Current.BlueIncrement)*B;			
			if(Current.BlueRemainder*2 >= dy) {
				B += Current.BlueIncrement;
				Current.BlueRemainder -= dy;
			}
			
			Current.Point.c.r = (float)R/255;
			Current.Point.c.g = (float)G/255;
			Current.Point.c.b = (float)B/255;
		}
		else {
			Current.Point.c.r = Current.Color.r;
			Current.Point.c.g = Current.Color.g;
			Current.Point.c.b = Current.Color.b;			
		}
		Current.Point.y += iy;
	}

	//next point for line with slope <= 1
	private static void LittleSlopeNextPoint(Point2D p1, Point2D p2, PointData Current) {
		int ix, iy, dred, dgreen, dblue, R, G, B;
		
		//calculate general info
		int dx = Math.abs(p2.x - p1.x);
		int dy = Math.abs(p2.y - p1.y);

		ix = (p2.x > p1.x) ? 1 : -1;
		iy = (p2.y > p1.y) ? 1 : -1;	
		
		dred = Math.abs(getIntColor(p2, "Red") - getIntColor(p1, "Red"));
		dgreen = Math.abs(getIntColor(p2, "Green") - getIntColor(p1, "Green"));
		dblue = Math.abs(getIntColor(p2, "Blue") - getIntColor(p1, "Blue"));
		
		//calculate decision parameter, two*delta_Y and two*delta_Y minus two_delta_X
		int YY = 2*dy;
		int YYXX = 2*(dy -dx);
			
		//get Next Point	
		if(Current.DecisionParameter < 0) 
			Current.DecisionParameter += YY; 
		else {
			Current.Point.y += iy;
			Current.DecisionParameter += YYXX;
		}
	
		// set color and get next point
		if(Current.doSmoothShading) {
			R = dred/dx;
			Current.RedRemainder += (dred - R*dx);
			R = getIntColor(Current.Point, "Red") + (Current.RedIncrement)*R;			
			if(Current.RedRemainder*2 >= dx) {
				R += Current.RedIncrement;
				Current.RedRemainder -= dx;
			}

			G = dgreen/dx;
			Current.GreenRemainder += (dgreen - G*dx);
			G = getIntColor(Current.Point, "Green") + (Current.GreenIncrement)*G;			
			if(Current.GreenRemainder*2 >= dx) {
				G += Current.GreenIncrement;
				Current.GreenRemainder -= dx;
			}
			
			B = dblue/dx;
			Current.BlueRemainder += (dblue - B*dx);
			B = getIntColor(Current.Point, "Blue") + (Current.BlueIncrement)*B;			
			if(Current.BlueRemainder*2 >= dx) {
				B += Current.BlueIncrement;
				Current.BlueRemainder -= dx;
			}
			
			Current.Point.c.r = (float)R/255;
			Current.Point.c.g = (float)G/255;
			Current.Point.c.b = (float)B/255;
		}
		else {
			Current.Point.c.r = Current.Color.r;
			Current.Point.c.g = Current.Color.g;
			Current.Point.c.b = Current.Color.b;	
		}
		Current.Point.x += ix;
	}

	//next point for line with slope > 1
	private static void LargeSlopeNextPoint(Point2D p1, Point2D p2, PointData Current) {
		int ix, iy, dred, dgreen, dblue, R, G, B;
		
		//calculate general info
		int dx = Math.abs(p2.x - p1.x);
		int dy = Math.abs(p2.y - p1.y);

		ix = (p2.x > p1.x) ? 1 : -1;
		iy = (p2.y > p1.y) ? 1 : -1;
		
		dred = Math.abs(getIntColor(p2, "Red") - getIntColor(p1, "Red"));
		dgreen = Math.abs(getIntColor(p2, "Green") - getIntColor(p1, "Green"));
		dblue = Math.abs(getIntColor(p2, "Blue") - getIntColor(p1, "Blue"));
	
		//calculate decision parameter, two*delta_Y and two*delta_Y minus two_delta_X
		int XX = 2*dx;
		int XXYY = 2*(dx -dy);
		
		//get next point 	
		if(Current.DecisionParameter < 0) 
			Current.DecisionParameter += XX; 
		else {
			Current.Point.x += ix;
			Current.DecisionParameter += XXYY;
		}
		//set color
		if(Current.doSmoothShading) {				
			R = dred/dy;
			Current.RedRemainder += (dred - R*dy);
			R = getIntColor(Current.Point, "Red") + (Current.RedIncrement)*R;			
			if(Current.RedRemainder*2 >= dy) {
				R += Current.RedIncrement;
				Current.RedRemainder -= dy;
			}

			G = dgreen/dy;
			Current.GreenRemainder += (dgreen - G*dy);
			G = getIntColor(Current.Point, "Green") + (Current.GreenIncrement)*G;			
			if(Current.GreenRemainder*2 >= dy) {
				G += Current.GreenIncrement;
				Current.GreenRemainder -= dy;
			}
			
			B = dblue/dy;
			Current.BlueRemainder += (dblue - B*dy);
			B = getIntColor(Current.Point, "Blue") + (Current.BlueIncrement)*B;			
			if(Current.BlueRemainder*2 >= dy) {
				B += Current.BlueIncrement;
				Current.BlueRemainder -= dy;
			}
			
			Current.Point.c.r = (float)R/255;
			Current.Point.c.g = (float)G/255;
			Current.Point.c.b = (float)B/255;	
		}
		else {
			Current.Point.c.r = Current.Color.r;
			Current.Point.c.g = Current.Color.g;
			Current.Point.c.b = Current.Color.b;
		}
		Current.Point.y += iy;
	}
	
	//get Next point
	private static void NextPoint(Point2D p1, Point2D p2, PointData Current) {
		if(Current.Slope == -1)
			NoSlopeNextPoint(p1, p2, Current);
		else if(Current.Slope < 1)
			LittleSlopeNextPoint(p1, p2, Current);
		else 
			LargeSlopeNextPoint(p1, p2, Current);
	}
	
	// draw a line segment
	public static void drawLine(BufferedImage buff, Point2D p1, Point2D p2)
	{
		PointData step = new PointData(p1);
		
		//calculate general info
		int dx = Math.abs(p2.x - p1.x);
		int dy = Math.abs(p2.y - p1.y);
		
		float _Slope = -1;
		if(dx != 0) {
			_Slope = (float)dy/dx;
			step.Slope = _Slope;
		}
		
		step.RedIncrement = (p2.c.r > p1.c.r) ? 1 : -1;
		step.GreenIncrement = (p2.c.g > p1.c.g) ? 1 : -1;
		step.BlueIncrement = (p2.c.b > p1.c.b) ? 1 : -1;
		
		// draw lines with 0 <= slope <= 1
		if(_Slope <= 1.0 && _Slope >= 0) {
			step.DecisionParameter = 2*dy - dx;
				
			while(step.Point.x != p2.x) {
				drawPoint(buff, step.Point);
				NextPoint(p1, p2, step);
			}
			drawPoint(buff, p2);
		} 
		else { //draw line for slope > 1 or slope = -1(no slope actually)	
			step.DecisionParameter = 2*dx - dy;
			
			while(step.Point.y != p2.y) {
				drawPoint(buff, step.Point);	
				NextPoint(p1, p2, step);
			}
			drawPoint(buff, p2);
		}
	}
	
	// draw random triangle
	public static void drawTriangle(BufferedImage buff, Point2D p1, Point2D p2, Point2D p3, boolean do_smooth, boolean do_fill)
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
		
		if(do_fill) {
			drawFlatBottomTriangle(buff, high, middle, low, do_smooth, p1.c);
			if(middle.y != low.y)
				drawFlatTopTriangle(buff, middle, high, low, do_smooth, p1.c);
		}
		else {
			drawLine(buff, p1, p3);
			drawLine(buff, p2, p3);
		}
	}
	
	//draw flat bottom triangle from top to bottom, p1 is top point
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
		
		begin.RedIncrement = (p2.c.r > p1.c.r) ? 1 : -1;
		begin.GreenIncrement = (p2.c.g > p1.c.g) ? 1 : -1;
		begin.BlueIncrement = (p2.c.b > p1.c.b) ? 1 : -1;
		
		int dx2 = Math.abs(p3.x - p1.x);
		int dy2 = Math.abs(p3.y - p1.y);
		
		end.RedIncrement = (p3.c.r > p1.c.r) ? 1 : -1;
		end.GreenIncrement = (p3.c.g > p1.c.g) ? 1 : -1;
		end.BlueIncrement = (p3.c.b > p1.c.b) ? 1 : -1;
		
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
				NextPoint(p1, p2, begin);
				}while(begin.Point.y != Increment+1);

			do {
				drawPoint(buff, end.Point);
				NextPoint(p1, p3, end);
				}while(end.Point.y != Increment+1);
			
			SketchBase.drawLine(buff, begin.Point, end.Point);
			Increment++;
		}
	}

	//draw flat top triangle from bottom to top, p3 is bottom point
	private static void drawFlatTopTriangle(BufferedImage buff, Point2D p1, Point2D p2, Point2D p3, boolean do_smooth, ColorType Color)
	{
		int Increment = p3.y;
		int Boundry = (p1.y > p2.y) ? p1.y : p2.y;
		PointData begin = new PointData(do_smooth, Color, p3);
		PointData end = new PointData(do_smooth, Color, p3);
		
		//calculate left edge slope and right edge slope
		int dx1 = Math.abs(p3.x - p1.x);
		int dy1 = Math.abs(p3.y - p1.y);
		
		begin.RedIncrement = (p1.c.r > p3.c.r) ? 1 : -1;
		begin.GreenIncrement = (p1.c.g > p3.c.g) ? 1 : -1;
		begin.BlueIncrement = (p1.c.b > p3.c.b) ? 1 : -1;
		
		float LeftSlope = -1;
		if(dx1 != 0) {
			LeftSlope = (float)dy1/dx1;
			begin.Slope = LeftSlope;
		}
		
		int dx2 = Math.abs(p3.x - p2.x);
		int dy2 = Math.abs(p3.y - p2.y);
		
		end.RedIncrement = (p2.c.r > p3.c.r) ? 1 : -1;
		end.GreenIncrement = (p2.c.g > p3.c.g) ? 1 : -1;
		end.BlueIncrement = (p2.c.b > p3.c.b) ? 1 : -1;
		
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
				NextPoint(p3, p1, begin);
			}while(begin.Point.y != Increment-1);

			do {
				drawPoint(buff, end.Point);
				NextPoint(p3, p2, end);
			}while(end.Point.y != Increment-1);
			
			SketchBase.drawLine(buff, begin.Point, end.Point);
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