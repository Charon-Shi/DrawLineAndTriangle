

public class PointData {
		public int DecisionParameter;	
		public float Slope;
		public boolean Smooth;
		public ColorType Color;
		public Point2D Point;
		
		PointData() {
			DecisionParameter = 0; 
			Slope = -1;
			Smooth = true;
		}

		PointData(Point2D _Point) { 
			DecisionParameter = 0; 
			Slope = -1;
			Smooth = true;
			Point = new Point2D(_Point); 
		}
		
		PointData(PointData _PointData) {
			DecisionParameter = _PointData.DecisionParameter;
			Point = new Point2D(_PointData.Point); 
			Slope = _PointData.Slope;
			Smooth = _PointData.Smooth;
		}
		
		PointData(boolean _Smooth, Point2D _Point) { 
			DecisionParameter = 0; 
			Slope = -1;
			Smooth = _Smooth;
			Point = new Point2D(_Point); 
		}
		
		PointData(boolean _Smooth, ColorType _Color, Point2D _Point) { 
			DecisionParameter = 0; 
			Slope = -1;
			Smooth = _Smooth;
			Color = _Color;
			Point = new Point2D(_Point); 
		}
	}