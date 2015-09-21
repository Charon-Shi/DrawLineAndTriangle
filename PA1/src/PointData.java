

public class PointData {
		public int DecisionParameter;	//for position
		public float Slope;
		
		public boolean doSmoothShading;
		public boolean doFillmode;
		public boolean doTextureMapping;
		
		public ColorType Color; // first point's color, used for non-smooth shading
		
		public int RedRemainder;
		public int RedIncrement;
		
		public int GreenRemainder;
		public int GreenIncrement;
		
		public int BlueRemainder;
		public int BlueIncrement;
		
		public Point2D Point;
		
		PointData() {
			DecisionParameter = 0; 
			RedRemainder = 0;
			GreenRemainder = 0;
			BlueRemainder = 0;
			Slope = -1;
			doSmoothShading = true;
		}

		PointData(Point2D _Point) { 
			DecisionParameter = 0; 
			RedRemainder = 0;
			GreenRemainder = 0;
			BlueRemainder = 0;
			Slope = -1;
			doSmoothShading = true;
			Point = new Point2D(_Point); 
		}
		
		PointData(PointData _PointData) {
			DecisionParameter = _PointData.DecisionParameter;
			RedRemainder = _PointData.RedRemainder;
			GreenRemainder = _PointData.GreenRemainder;
			BlueRemainder = _PointData.BlueRemainder;
			Point = new Point2D(_PointData.Point); 
			Slope = _PointData.Slope;
			doSmoothShading = _PointData.doSmoothShading;
		}
		
		PointData(boolean _doSmoothFill, Point2D _Point) { 
			DecisionParameter = 0; 
			RedRemainder = 0;
			GreenRemainder = 0;
			BlueRemainder = 0;
			Slope = -1;
			doSmoothShading = _doSmoothFill;
			Point = new Point2D(_Point); 
		}
		
		PointData(boolean _doSmoothFill, ColorType _Color, Point2D _Point) { 
			DecisionParameter = 0; 
			RedRemainder = 0;
			GreenRemainder = 0;
			BlueRemainder = 0;
			Slope = -1;
			doSmoothShading = _doSmoothFill;
			Color = _Color;
			Point = new Point2D(_Point); 
		}
	}