package com.example.countrycityselect;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.countrycityselect.SeleteDateActivity.endBean;
import static com.example.countrycityselect.SeleteDateActivity.startBean;

public class DateFragment extends Fragment {

    private View root;
    private RecyclerView recyclerView;
    private AD ad;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_date, container, false);
            recyclerView = root.findViewById(R.id.rv);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        }
        initData();
        return root;
    }

    private void initData() {
        ArrayList<DateBean> dates = new ArrayList<>();
        int month = getArguments().getInt("month");
        int year = SeleteDateActivity.year;
        int currentMonth = SeleteDateActivity.month;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, currentMonth);
        calendar.add(Calendar.MONTH, month);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DATE, 1);
        calendar.roll(Calendar.DATE, -1);
        int maxDays = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int wek = calendar.get(Calendar.DAY_OF_WEEK);
        int currentYear = calendar.get(Calendar.YEAR);
        int newMonth = calendar.get(Calendar.MONTH);
        for (int i = 1; i <= (maxDays + wek - 1); i++) {
            if (i < wek) {
                dates.add(new DateBean());
            } else {
                int day = (i - wek) + 1;
                if (day <= maxDays) {
                    dates.add(new DateBean(currentYear, newMonth + 1, day));
                } else {
                    dates.add(new DateBean());
                }
            }
        }
        ad = new AD(dates);
        recyclerView.setAdapter(ad);
        /*String Week = "";
        if (wek == 1) {
            Week += "星期日";
        }
        if (wek == 2) {
            Week += "星期一";
        }
        if (wek == 3) {
            Week += "星期二";
        }
        if (wek == 4) {
            Week += "星期三";
        }
        if (wek == 5) {
            Week += "星期四";
        }
        if (wek == 6) {
            Week += "星期五";
        }
        if (wek == 7) {
            Week += "星期六";
        }*/
    }

    class AD extends RecyclerView.Adapter<VH> {
        private final ArrayList<DateBean> dates;

        public AD(ArrayList<DateBean> dates) {
            this.dates = dates;
        }

        public ArrayList<DateBean> getDates() {
            return dates;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new VH(View.inflate(getActivity(), R.layout.item_rv_date, null));
        }

        @Override
        public void onBindViewHolder(@NonNull final VH vh, int i) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) vh.textView.getLayoutParams();
            layoutParams.width = (int) (ScreenTool.getScreenWidth(getActivity()) * 1.0f / 7);
            layoutParams.height = (int) (ScreenTool.getScreenWidth(getActivity()) * 1.0f / 7);
            layoutParams.gravity = Gravity.CENTER;
            vh.textView.setLayoutParams(layoutParams);
            final DateBean dateBean = dates.get(i);
            Calendar calendar = Calendar.getInstance();
            final int y = calendar.get(Calendar.YEAR);
            final int m = calendar.get(Calendar.MONTH);
            final int d = calendar.get(Calendar.DAY_OF_MONTH);
            if (dateBean.getYear() == y && dateBean.getMonth() == (m + 1) && dateBean.getDay() == d) {
                vh.textView.setEnabled(false);
            } else {
                vh.textView.setEnabled(true);
            }
            vh.textView.setSelected(dateBean.isSeleted());
            vh.textView.setText(dateBean.getDay() == 0 ? "" : "" + dateBean.getDay());
            vh.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dateBean.getYear() != 0) {
                        boolean b = false;
                        if (dateBean.getYear() == y && dateBean.getMonth() == (m + 1)) {
                            if (dateBean.getDay() > d) {
                                b = true;
                            }
                        } else if (dateBean.getYear() > y || dateBean.getMonth() > (m + 1)) {
                            b = true;
                        }
                        if (b) {
                            boolean hasStartSelected = false;
                            for (DateBean date : dates) {
                                if (date.isSeleted() || startBean != null) {
                                    hasStartSelected = true;
                                    break;
                                }
                            }
                            if (hasStartSelected) {
                                //已经有了开始时间了
                                for (int i1 = 0; i1 < dates.size(); i1++) {
                                    if (!dates.get(i1).equal(startBean)) {
                                        dates.get(i1).setSeleted(false);
                                        dates.get(i1).setStart(false);
                                        dates.get(i1).setEnd(false);
                                    }
                                }
                                if (dateBean.isBigWith(startBean)) {
                                    try {
                                        long daysOfTwoDate = daysOfTwoDate(dateBean.formatHHMMDD(), startBean.formatHHMMDD());
                                        if (daysOfTwoDate > 60) {
                                            Toast.makeText(getContext(), "开始时间与结束时间相差不可超过60天", Toast.LENGTH_SHORT).show();
                                        } else {
                                            dateBean.setSeleted(true);
                                            dateBean.setEnd(true);
                                            dateBean.setStart(false);
                                            endBean = dateBean;
                                            notifyDataSetChanged();
                                            if (getActivity() instanceof SeleteDateActivity) {
                                                ((SeleteDateActivity) getActivity()).refreshOthers();
                                            }
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(getContext(), "结束时间不可小于开始时间", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                //没有选择开始时间 现在开始选择
                                for (DateBean date : dates) {
                                    date.setSeleted(false);
                                    date.setEnd(false);
                                    date.setStart(false);
                                }
                                dateBean.setSeleted(true);
                                dateBean.setStart(true);
                                startBean = dateBean;
                                notifyDataSetChanged();
                            }
                        }
                    }
                    Log.e("--", dateBean.toString());
                }
            });
            if (dateBean.isStart()) {
                vh.textView.setBackgroundResource(R.drawable.selector_start);
            } else if (dateBean.isEnd()) {
                vh.textView.setBackgroundResource(R.drawable.selector_end);
            } else {
                vh.textView.setBackgroundResource(R.drawable.selector_date_item);
            }
        }

        @Override
        public int getItemCount() {
            return dates == null ? 0 : dates.size();
        }
    }

    class VH extends RecyclerView.ViewHolder {

        private final TextView textView;

        public VH(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv);
        }
    }

    public void refresh() {
        for (DateBean date : ad.getDates()) {
            if (date.equal(startBean) || date.equal(endBean)) {
                date.setSeleted(true);
                date.setStart(false);
                date.setEnd(false);
                if (date.equal(startBean)) {
                    date.setStart(true);
                } else {
                    date.setEnd(true);
                }
            } else {
                date.setSeleted(false);
                date.setStart(false);
                date.setEnd(false);
            }
        }
        ad.notifyDataSetChanged();
    }

    public long daysOfTwoDate(String date2, String date1) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fDate = sdf.parse(date1);
        Date oDate = sdf.parse(date2);
        long days = (oDate.getTime() - fDate.getTime()) / (1000 * 3600 * 24);
        System.out.print(days);
        return days;
    }
}
