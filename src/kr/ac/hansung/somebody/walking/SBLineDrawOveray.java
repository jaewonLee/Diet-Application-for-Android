package kr.ac.hansung.somebody.walking;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * Android maps의 Overlay클래스를 상속받아, map위에 이동 거리 표시 구현 클래스 (출처 :
 * http://dev.naver.com/projects/bestdriverapp)
 * 
 * @author Jaewon Lee(jaewon87@naver.com)
 * @version 1.0
 */
public class SBLineDrawOveray extends Overlay {
	ArrayList<Location> locations;
	GeoPoint fromGeoPoint;
	GeoPoint toGeoPoint;
	int startPoint = 0; // 현재 그려줄 점의 위치이다.
	int endPoint = 0; // 현재 그려줄 점의 위치이다.
	Double geoLat;
	Double geoLng;

	boolean startSign = false;

	// 처음의 생성 시
	public SBLineDrawOveray() {
		locations = new ArrayList<Location>();
	}

	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (startSign == true) { // 처음의 생성 시
			super.draw(canvas, mapView, shadow);

			Paint paint = new Paint();
			paint.setStrokeWidth(3);
			paint.setARGB(255, 255, 0, 0);

			// 선이 이어진다.
			if (locations.size() >= 2) {
				for (int i = 0; i < endPoint; i++) {
					fromGeoPoint = changeLocationToGeoPoint(locations.get(i));
					Point fromPixPoint = new Point();

					toGeoPoint = changeLocationToGeoPoint(locations.get(i + 1));
					Point toPixPoint = new Point();

					mapView.getProjection()
							.toPixels(fromGeoPoint, fromPixPoint);
					mapView.getProjection().toPixels(toGeoPoint, toPixPoint);

					canvas.drawLine(fromPixPoint.x, fromPixPoint.y,
							toPixPoint.x, toPixPoint.y, paint);
				}
			} else {
				;
			}
		}
	}

	public GeoPoint changeLocationToGeoPoint(Location location) {
		Double geoLat;
		Double geoLng;
		GeoPoint geoPoint;

		// Location 객체에 저장된 위치정보를 지도에 표현되는 정보로 변환
		geoLat = location.getLatitude() * 1E6;
		geoLng = location.getLongitude() * 1E6;
		geoPoint = new GeoPoint(geoLat.intValue(), geoLng.intValue());

		return geoPoint;
	}

	public void setLocation(Location location) {
		if (startSign == false) { // 처음 정보를 받았을 때
			locations.add(location);
			endPoint = startPoint + 1;
			startSign = true;
		} else if (startSign = true && locations.size() <= 1) { // 두번째 정보를 받았을 때
			locations.add(location);
		} else if (startSign = true && locations.size() >= 2) { // 3개 째의 정보를 받을때
														
			locations.add(location);
			startPoint = endPoint;
			endPoint = endPoint + 1;
		} else {
			;
		}
	}
}
