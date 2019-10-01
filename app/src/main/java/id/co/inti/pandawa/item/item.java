package id.co.inti.pandawa.item;

import android.graphics.Bitmap;

public class item {

        String names,temp,humid,baterai;
        int count;
        Bitmap bm;



        public void setImage(Bitmap kodetk) {
        this.bm = kodetk;
    }
        public Bitmap getImage(){
        return bm;
    }

        public void setName(String name)
        {
            this.names=name;
        }
        public String getName() {
        return names;
    }
        public void setTemp(String temps)
        {
            this.temp=temps;
        }
        public String getTemp()
        {
            return temp;
        }

        public void setHumid(String hum)
        {
            this.humid= hum;
        }
        public String getHumid()
        {
            return humid;
        }

        public void setBaterai(String bat){ this.baterai=bat;}
        public String getBaterai(){ return baterai;}

    public void setCount(int counts){ this.count=counts;}
    public int getCount(){ return count;}




}
