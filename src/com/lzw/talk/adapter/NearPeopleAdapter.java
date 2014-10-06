package com.lzw.talk.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.avos.avoscloud.AVGeoPoint;
import com.lzw.talk.R;
import com.lzw.talk.avobject.User;
import com.lzw.talk.base.App;
import com.lzw.talk.service.PrefDao;
import com.lzw.talk.ui.view.ViewHolder;
import com.lzw.talk.util.PhotoUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;

public class NearPeopleAdapter extends BaseListAdapter<User> {
  PrettyTime prettyTime;

  public NearPeopleAdapter(Context ctx) {
    super(ctx);
    init();
  }

  private void init() {
    prettyTime=new PrettyTime();
  }

  public NearPeopleAdapter(Context ctx, List<User> datas) {
    super(ctx, datas);
    init();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = inflater.inflate(R.layout.item_near_people, null);
    }
    final User contract = datas.get(position);
    TextView nameView = ViewHolder.findViewById(convertView, R.id.name_text);
    TextView distanceView = ViewHolder.findViewById(convertView, R.id.distance_text);
    TextView logintimView = ViewHolder.findViewById(convertView, R.id.login_time_text);
    ImageView avatarView = ViewHolder.findViewById(convertView, R.id.avatar_view);
    String avatar = contract.getAvatarUrl();
    if (avatar != null && !avatar.equals("")) {
      ImageLoader.getInstance().displayImage(avatar, avatarView,
          PhotoUtil.getImageLoaderOptions());
    } else {
      avatarView.setImageResource(R.drawable.default_avatar);
    }
    AVGeoPoint geoPoint = contract.getLocation();
    PrefDao prefDao = PrefDao.getCurUserPrefDao(ctx);
    AVGeoPoint location = prefDao.getLocation();
    String currentLat = String.valueOf(location.getLatitude());
    String currentLong = String.valueOf(location.getLongitude());
    if (geoPoint != null && !currentLat.equals("") && !currentLong.equals("")) {
      double distance = DistanceOfTwoPoints(Double.parseDouble(currentLat), Double.parseDouble(currentLong), contract.getLocation().getLatitude(),
          contract.getLocation().getLongitude());
      distanceView.setText(String.valueOf(distance) + App.ctx.getString(R.string.metre));
    } else {
      distanceView.setText(App.ctx.getString(R.string.unknown));
    }
    nameView.setText(contract.getUsername());
    Date updatedAt = contract.getUpdatedAt();
    String prettyTimeStr = this.prettyTime.format(updatedAt);
    logintimView.setText(App.ctx.getString(R.string.recent_login_time) +prettyTimeStr);
    return convertView;
  }

  private static final double EARTH_RADIUS = 6378137;

  private static double rad(double d) {
    return d * Math.PI / 180.0;
  }

  /**
   * 根据两点间经纬度坐标（double值），计算两点间距离，
   *
   * @param lat1
   * @param lng1
   * @param lat2
   * @param lng2
   * @return 距离：单位为米
   */
  public static double DistanceOfTwoPoints(double lat1, double lng1,
                                           double lat2, double lng2) {
    double radLat1 = rad(lat1);
    double radLat2 = rad(lat2);
    double a = radLat1 - radLat2;
    double b = rad(lng1) - rad(lng2);
    double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
        + Math.cos(radLat1) * Math.cos(radLat2)
        * Math.pow(Math.sin(b / 2), 2)));
    s = s * EARTH_RADIUS;
    s = Math.round(s * 10000) / 10000;
    return s;
  }

}
