package kr.ac.hansung.somebody.walking;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

/**
 * Android maps의 Overlay클래스를 상속받아, map위에 이미지 표시 구현 클래스
 * (출처 : http://dev.naver.com/projects/bestdriverapp)
 * @author Jaewon Lee(jaewon87@naver.com)
 * @version 1.0
 */
public class MyOverlay extends Overlay {
	int locationSet;
	Location location;
	private final int mRadius = 5;
	



	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		Projection projection = mapView.getProjection();
		location.getSpeed();


		if (shadow == false && location != null) {
			Double latitude = location.getLatitude() * 1E6;
			Double longitude = location.getLongitude() * 1E6;
			GeoPoint geoPoint;
			geoPoint = new GeoPoint(latitude.intValue(), longitude.intValue());

			Point point = new Point();
			projection.toPixels(geoPoint, point);

			RectF oval = new RectF(point.x - mRadius, point.y - mRadius,
					point.x + mRadius, point.y + mRadius);

			Paint paint = new Paint();
			paint.setARGB(250, 255, 255, 255);
			paint.setAntiAlias(true);
			paint.setFakeBoldText(true);

			Paint backPaint = new Paint();
			backPaint.setARGB(175, 50, 50, 50);
			backPaint.setAntiAlias(true);

			RectF backRect = new RectF(point.x + 2 + mRadius, point.y - 3
					* mRadius, point.x + 65, point.y + mRadius);

			canvas.drawOval(oval, paint);
			canvas.drawRoundRect(backRect, 5, 5, backPaint);
			if(locationSet == 1){
				canvas.drawText("출발지 위치", point.x + 2 * mRadius, point.y, paint);
			}else if(locationSet == 2){
				canvas.drawText("목적지 위치", point.x + 2 * mRadius, point.y, paint);
			}else{
				canvas.drawText("your grave", point.x + 2 * mRadius, point.y, paint);
			}
		}
		super.draw(canvas, mapView, shadow);
	}

	public boolean onTap(GeoPoint point, MapView mapView) {
		
		return false;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location, int locationSet) {
		this.location = location;
		this.locationSet = locationSet;
	}
}
