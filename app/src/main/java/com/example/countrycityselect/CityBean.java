package com.example.countrycityselect;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class CityBean implements Parcelable {
    private CountryRegionBean CountryRegion;

    public CountryRegionBean getCountryRegion() {
        return CountryRegion;
    }

    public void setCountryRegion(CountryRegionBean countryRegion) {
        CountryRegion = countryRegion;
    }

    public static class CountryRegionBean implements Parcelable {
        public String getCode() {
            return Code;
        }

        public void setCode(String code) {
            Code = code;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public ArrayList<StateBean> getStates() {
            return States;
        }

        public void setStates(ArrayList<StateBean> states) {
            States = states;
        }

        private String Code;
        private String Name;
        private ArrayList<StateBean> States;

        static class StateBean implements Parcelable {
            public String getCode() {
                return Code;
            }

            public void setCode(String code) {
                Code = code;
            }

            public String getName() {
                return Name;
            }

            public void setName(String name) {
                Name = name;
            }


            private String Code;
            private String Name;
            private ArrayList<CityB> citys;

            public ArrayList<CityB> getCitys() {
                return citys;
            }

            public void setCitys(ArrayList<CityB> citys) {
                this.citys = citys;
            }

            static class CityB implements Parcelable {
                public String getCode() {
                    return Code;
                }

                public void setCode(String code) {
                    Code = code;
                }

                public String getName() {
                    return Name;
                }

                public void setName(String name) {
                    Name = name;
                }

                private String Code;
                private String Name;

                public RegionBean getRegion() {
                    return Region;
                }

                public void setRegion(RegionBean region) {
                    Region = region;
                }

                private RegionBean Region;

                static class RegionBean implements Parcelable {
                    public String getCode() {
                        return Code;
                    }

                    public void setCode(String code) {
                        Code = code;
                    }

                    public String getName() {
                        return Name;
                    }

                    public void setName(String name) {
                        Name = name;
                    }

                    private String Code;
                    private String Name;

                    @Override
                    public int describeContents() {
                        return 0;
                    }

                    @Override
                    public void writeToParcel(Parcel dest, int flags) {
                        dest.writeString(this.Code);
                        dest.writeString(this.Name);
                    }

                    public RegionBean() {
                    }

                    protected RegionBean(Parcel in) {
                        this.Code = in.readString();
                        this.Name = in.readString();
                    }

                    public static final Creator<RegionBean> CREATOR = new Creator<RegionBean>() {
                        @Override
                        public RegionBean createFromParcel(Parcel source) {
                            return new RegionBean(source);
                        }

                        @Override
                        public RegionBean[] newArray(int size) {
                            return new RegionBean[size];
                        }
                    };
                }

                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {
                    dest.writeString(this.Code);
                    dest.writeString(this.Name);
                    dest.writeParcelable(this.Region, flags);
                }

                public CityB() {
                }

                protected CityB(Parcel in) {
                    this.Code = in.readString();
                    this.Name = in.readString();
                    this.Region = in.readParcelable(RegionBean.class.getClassLoader());
                }

                public static final Creator<CityB> CREATOR = new Creator<CityB>() {
                    @Override
                    public CityB createFromParcel(Parcel source) {
                        return new CityB(source);
                    }

                    @Override
                    public CityB[] newArray(int size) {
                        return new CityB[size];
                    }
                };
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.Code);
                dest.writeString(this.Name);
                dest.writeList(this.citys);
            }

            public StateBean() {
            }

            protected StateBean(Parcel in) {
                this.Code = in.readString();
                this.Name = in.readString();
                this.citys = new ArrayList<CityB>();
                in.readList(this.citys, CityB.class.getClassLoader());
            }

            public static final Creator<StateBean> CREATOR = new Creator<StateBean>() {
                @Override
                public StateBean createFromParcel(Parcel source) {
                    return new StateBean(source);
                }

                @Override
                public StateBean[] newArray(int size) {
                    return new StateBean[size];
                }
            };
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.Code);
            dest.writeString(this.Name);
            dest.writeList(this.States);
        }

        public CountryRegionBean() {
        }

        protected CountryRegionBean(Parcel in) {
            this.Code = in.readString();
            this.Name = in.readString();
            this.States = new ArrayList<StateBean>();
            in.readList(this.States, StateBean.class.getClassLoader());
        }

        public static final Creator<CountryRegionBean> CREATOR = new Creator<CountryRegionBean>() {
            @Override
            public CountryRegionBean createFromParcel(Parcel source) {
                return new CountryRegionBean(source);
            }

            @Override
            public CountryRegionBean[] newArray(int size) {
                return new CountryRegionBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.CountryRegion, flags);
    }

    public CityBean() {
    }

    protected CityBean(Parcel in) {
        this.CountryRegion = in.readParcelable(CountryRegionBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<CityBean> CREATOR = new Parcelable.Creator<CityBean>() {
        @Override
        public CityBean createFromParcel(Parcel source) {
            return new CityBean(source);
        }

        @Override
        public CityBean[] newArray(int size) {
            return new CityBean[size];
        }
    };
}
