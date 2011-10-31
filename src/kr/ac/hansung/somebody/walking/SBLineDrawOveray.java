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
 * Android maps�� OverlayŬ������ ��ӹ޾�, map���� �̵� �Ÿ� ǥ�� ���� Ŭ���� (��ó :
 * http://dev.naver.com/projects/bestdriverapp)
 * 
 * @author Jaewon Lee(jaewon87@naver.com)
 * @version 1.0
 */
public class SBLineDrawOveray extends Overlay {
	ArrayList<Location> locations;
	GeoPoint fromGeoPoint;
	GeoPoint toGeoPoint;
	int startPoint = 0; // ���� �׷��� ���� ��ġ�̴�.
	int endPoint = 0; // ���� �׷��� ���� ��ġ�̴�.
	Double geoLat;
	Double geoLng;

	boolean startSign = false;

	// ó���� ���� ��
	public SBLineDrawOveray() {
		locations = new ArrayList<Location>();
	}

	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (startSign == true) { // ó���� ���� ��
			super.draw(canvas, mapView, shadow);

			Paint paint = new Paint();
			paint.setStrokeWidth(3);
			paint.setARGB(255, 255, 0, 0);

			// ���� �̾�����.
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

		// Location ��ü�� ����� ��ġ������ ������ ǥ���Ǵ� ������ ��ȯ
		geoLat = location.getLatitude() * 1E6;
		geoLng = location.getLongitude() * 1E6;
		geoPoint = new GeoPoint(geoLat.intValue(), geoLng.intValue());

		return geoPoint;
	}

	public void setLocation(Location location) {
		if (startSign == false) { // ó�� ������ �޾��� ��
			locations.add(location);
			endPoint = startPoint + 1;
			startSign = true;
		} else if (startSign = true && locations.size() <= 1) { // �ι�° ������ �޾��� ��
			locations.add(location);
		} else if (startSign = true && locations.size() >= 2) { // 3�� °�� ������ ������
														
			locations.add(location);
			startPoint = endPoint;
			endPoint = endPoint + 1;
		} else {
			;
		}
	}
}
