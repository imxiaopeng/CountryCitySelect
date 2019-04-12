package com.example.countrycityselect;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class Ad extends RecyclerView.Adapter<VH> {
    private final Context context;
    private List<CityBean> datas;
    public static final int FLAG_COUNTRY = 0;
    public static final int FLAG_STATE = 1;
    public static final int FLAG_CITY = 2;
    public int flag = FLAG_COUNTRY;
    public int index_country = 0;
    public int index_state = 0;
    public int index_city = 0;
    private boolean enableLoadMore = false;

    public void setEnableLoadMore(boolean enableLoadMore) {
        this.enableLoadMore = enableLoadMore;
    }

    public Ad(Context context, List<CityBean> datas, int flag) {
        this.datas = datas;
        this.flag = flag;
        this.context = context;
    }

    public int getIndex_country() {
        return index_country;
    }

    public void setIndex_country(int index_country) {
        this.index_country = index_country;
    }

    public int getIndex_state() {
        return index_state;
    }

    public void setIndex_state(int index_state) {
        this.index_state = index_state;
    }

    public int getIndex_city() {
        return index_city;
    }

    public void setIndex_city(int index_city) {
        this.index_city = index_city;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new VH(View.inflate(context, R.layout.item_rv, null));
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int i);
    }

    OnItemClickListener onItemClickListener = null;
    OnResultListener onResultListener = null;

    public void setOnResultListener(OnResultListener onResultListener) {
        this.onResultListener = onResultListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnResultListener {
        void onResult(Area area);
    }

    @Override
    public void onBindViewHolder(@NonNull VH vh, final int i) {
        switch (flag) {
            case FLAG_COUNTRY:
                final CityBean cityBean = datas.get(i);
                vh.tv.setText(cityBean.getCountryRegion().getName());
                vh.tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        index_country = i;
                        flag = FLAG_STATE;
                        if (cityBean.getCountryRegion().getStates().isEmpty()) {
                            if (onResultListener != null) {
                                Area area = new Area(cityBean.getCountryRegion().getName(), null, null);
                                onResultListener.onResult(area);
                            }
                            return;
                        }
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(v, i);
                        }
                    }
                });
                break;
            case FLAG_STATE:
                CityBean cityBean1 = datas.get(index_country);
                if (cityBean1.getCountryRegion().getStates().size() == 1) {
                    vh.tv.setText(cityBean1.getCountryRegion().getStates().get(0).getCitys().get(i).getName());
                    vh.tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CityBean.CountryRegionBean countryRegion = datas.get(index_country).getCountryRegion();
                            CityBean.CountryRegionBean.StateBean stateBean = countryRegion.getStates().get(0);
                            CityBean.CountryRegionBean.StateBean.CityB cityB = stateBean.getCitys().get(i);
                            String country = countryRegion.getName();
                            String state = stateBean.getName();
                            state = state == null ? "" : state;
                            String city = cityB.getName();
                            Area area = new Area(country, state, city);
                            if (onResultListener != null) {
                                onResultListener.onResult(area);
                            }
                        }
                    });
                } else {
                    vh.tv.setText(cityBean1.getCountryRegion().getStates().get(i).getName());
                    vh.tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            index_state = i;
                            flag = FLAG_CITY;
                            notifyDataSetChanged();
                        }
                    });
                }

                break;
            case FLAG_CITY:
                CityBean cityBean2 = datas.get(index_country);
                vh.tv.setText(cityBean2.getCountryRegion().getStates().get(index_state).getCitys().get(i).getName());
                vh.tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        index_city = i;
                        CityBean.CountryRegionBean countryRegion = datas.get(index_country).getCountryRegion();
                        CityBean.CountryRegionBean.StateBean stateBean = countryRegion.getStates().get(index_state);
                        CityBean.CountryRegionBean.StateBean.CityB cityB = stateBean.getCitys().get(i);
                        String country = countryRegion.getName();
                        String state = stateBean.getName();
                        state = state == null ? "" : state;
                        String city = cityB.getName();
                        Area area = new Area(country, state, city);
                        if (onResultListener != null) {
                            onResultListener.onResult(area);
                        }
                    }
                });
                break;
        }

    }

    @Override
    public int getItemCount() {
        int size = datas == null ? 0 : enableLoadMore ? 80 : datas.size();
        if (datas != null) {
            switch (flag) {
                case FLAG_COUNTRY:

                    break;
                case FLAG_STATE:
                    size = datas.get(index_country).getCountryRegion().getStates().size();
                    if (size == 1) {
                        size = datas.get(index_country).getCountryRegion().getStates().get(0).getCitys().size();
                    }
                    break;
                case FLAG_CITY:
                    size = datas.get(index_country).getCountryRegion().getStates().get(index_state).getCitys().size();
                    break;
            }
        }
        return size;
    }
}

class VH extends RecyclerView.ViewHolder {

    public TextView tv;

    public VH(@NonNull View itemView) {
        super(itemView);
        tv = itemView.findViewById(R.id.tv);
    }
}
