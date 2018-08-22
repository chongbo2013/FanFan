package com.fanfan.youtu.api.baidu.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Activate implements Parcelable {


    /**
     * log_id : 153301498125905
     * result : {"license":"5513EE32720EBA337BB216C7EF21C7AC1928153EA97571679BB305576EFC4A4B211C4649D6E5F17722D5429398D99994F8D5F168675606ECD2A04737EFCB6BACDC4F9CCEDA7A9A4493BD752A1B52D548B54469CAE87E1BDDEE99A2970AE162C2D9A9463CA6AA579848EC8016509D42666D700429761A763757767B97EA8821C44505173B239041F32A99B62647144DC843B6F1647A0BFFA1AE4D1612E5DE5EB4FAC761523B76980E8A6E147F6B0744E1E11AD28DCD2F6AF1082EF9F5C43062C1C725C4232694A927BAA16F21B291EAD75414D2DCA73DC2842340B006C9F3769F70F95C51E5FDEA60F7C009947327C7B5C1052D585DF7007EDFF99285D8529FD9,7B0AA1DE5E902C96D2756DBA97EE1046622DB3D1DD1F44E92D430998F250A46D65F04D6E4AB6BFA06002022F45446640A35B92A11BCC786123BE9453B743208E834E263151FFA1E9DBA3BC62BD2DE50EB71252ABDF33E0FEDFF641AE5CBE52948D719E63C902700DF02BEDDBB98B918EECEBD8EB9839A73F02FC4FD6BAC1A01935E65F5CC37D19A1DCF461D35670BE0DC69179D0D54D62C1435FF134B85FA3BB4FC9468E47820AF5BB39B008DFA461E835DC54D1EAA0CA5AC0D95652DB1792D5ACBD54EBE11321C8BC4985684995BB7E24E01330AA0736A2644700F0BE04544A3DBC8163382E61A88BE197BC4E745CD4A6FACA914F892DFD4852AEB0E5CBF652","cache":0}
     */

    private long log_id;
    private ResultBean result;

    public long getLog_id() {
        return log_id;
    }

    public void setLog_id(long log_id) {
        this.log_id = log_id;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean implements Parcelable {
        /**
         * license : 5513EE32720EBA337BB216C7EF21C7AC1928153EA97571679BB305576EFC4A4B211C4649D6E5F17722D5429398D99994F8D5F168675606ECD2A04737EFCB6BACDC4F9CCEDA7A9A4493BD752A1B52D548B54469CAE87E1BDDEE99A2970AE162C2D9A9463CA6AA579848EC8016509D42666D700429761A763757767B97EA8821C44505173B239041F32A99B62647144DC843B6F1647A0BFFA1AE4D1612E5DE5EB4FAC761523B76980E8A6E147F6B0744E1E11AD28DCD2F6AF1082EF9F5C43062C1C725C4232694A927BAA16F21B291EAD75414D2DCA73DC2842340B006C9F3769F70F95C51E5FDEA60F7C009947327C7B5C1052D585DF7007EDFF99285D8529FD9,7B0AA1DE5E902C96D2756DBA97EE1046622DB3D1DD1F44E92D430998F250A46D65F04D6E4AB6BFA06002022F45446640A35B92A11BCC786123BE9453B743208E834E263151FFA1E9DBA3BC62BD2DE50EB71252ABDF33E0FEDFF641AE5CBE52948D719E63C902700DF02BEDDBB98B918EECEBD8EB9839A73F02FC4FD6BAC1A01935E65F5CC37D19A1DCF461D35670BE0DC69179D0D54D62C1435FF134B85FA3BB4FC9468E47820AF5BB39B008DFA461E835DC54D1EAA0CA5AC0D95652DB1792D5ACBD54EBE11321C8BC4985684995BB7E24E01330AA0736A2644700F0BE04544A3DBC8163382E61A88BE197BC4E745CD4A6FACA914F892DFD4852AEB0E5CBF652
         * cache : 0
         */

        private String license;
        private int cache;

        public String getLicense() {
            return license;
        }

        public void setLicense(String license) {
            this.license = license;
        }

        public int getCache() {
            return cache;
        }

        public void setCache(int cache) {
            this.cache = cache;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.license);
            dest.writeInt(this.cache);
        }

        public ResultBean() {
        }

        protected ResultBean(Parcel in) {
            this.license = in.readString();
            this.cache = in.readInt();
        }

        public static final Creator<ResultBean> CREATOR = new Creator<ResultBean>() {
            @Override
            public ResultBean createFromParcel(Parcel source) {
                return new ResultBean(source);
            }

            @Override
            public ResultBean[] newArray(int size) {
                return new ResultBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.log_id);
        dest.writeParcelable(this.result, flags);
    }

    public Activate() {
    }

    protected Activate(Parcel in) {
        this.log_id = in.readLong();
        this.result = in.readParcelable(ResultBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<Activate> CREATOR = new Parcelable.Creator<Activate>() {
        @Override
        public Activate createFromParcel(Parcel source) {
            return new Activate(source);
        }

        @Override
        public Activate[] newArray(int size) {
            return new Activate[size];
        }
    };
}
