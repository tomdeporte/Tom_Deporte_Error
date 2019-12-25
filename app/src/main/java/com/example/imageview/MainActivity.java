package com.example.imageview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.lang.Math;

import java.util.Random;
import androidx.appcompat.app.AppCompatActivity;
import  android.renderscript.Allocation;



public class MainActivity extends AppCompatActivity {

    int[] CustomHSVtoRGB(float[] hsv){

        int rgb[]=new int[3];
        int ti = (int)(hsv[0]/60)%6;
        int f=(int)(hsv[0]/60)-ti;
        int l=(int)(hsv[2]*(1-hsv[1]));
        int m = (int)(hsv[2]*(1-f*hsv[1]));
        int n = (int)(hsv[2]*(1-f)*hsv[1]);
        switch(ti){
            case 0:
                rgb[0]=(int)hsv[2];
                rgb[1]=n;
                rgb[2]=l;
                break;
            case 1:
                rgb[0]=m;
                rgb[1]=(int)hsv[2];
                rgb[2]=l;
                break;
            case 2:
                rgb[0]=l;
                rgb[1]=(int)hsv[2];
                rgb[2]=n;
                break;
            case 3:
                rgb[0]=l;
                rgb[1]=m;
                rgb[2]=(int)hsv[2];
                break;
            case 4:
                rgb[0]=n;
                rgb[1]=l;
                rgb[2]=(int)hsv[2];
                break;
            case 5:
                rgb[0]=(int)hsv[2];
                rgb[1]=l;
                rgb[2]=m;
                break;
            default:
                rgb[0]=0;
                rgb[1]=0;
                rgb[2]=0;
                break;
        }
        return rgb;
    }

    float[] CustomColortoHSV(int pixel){
        float[] hsv= new float[3];
        int[] rgb = {Color.red(pixel), Color.green(pixel),Color.blue(pixel)};
        float min=rgb[0];
        float max=rgb[0];
        for(int i=0;i<rgb.length;i++){
            if(rgb[i]<min){
                min=rgb[i];
            }
            if(rgb[i]>max){
                max=rgb[i];
            }
        }
        if(max==min) {
            hsv[0] = 0;
        }else{
            if(max == rgb[0]){
                hsv[0]= (float)(((60)*((rgb[1]-rgb[2])/(max-min))+(360) % 360));
            }
            else{
                if (max == rgb[1]){
                    hsv[0]= (float) ((60)*((rgb[2]-rgb[0])/(max-min))+(120));
                }
                else{
                    if (max == rgb[2]){
                        hsv[0]= (float) ((60)*((rgb[0]-rgb[1])/(max-min))+(240));
                    }
                }
            }
        }
        if(max==0){
            hsv[1]=0;
        }else{
            hsv[1]=1-(min/max);
        }
        hsv[2]=max;
        return hsv;
    }

    int pixelToGray(int pixel){
        int r = Color.red(pixel);
        int g = Color.green(pixel);
        int b = Color.blue(pixel);

        return (r + b + g) / 3;
    }
/*
    private  void  pixelToGrayRS(Bitmap  bmp) {
        //1)  Creer un  contexte
        RenderScriptRenderScript  rs = RenderScript.create(this);
        //2)  Creer  des  Allocations  pour  passer  les  donnees
        Allocation  input = Allocation.createFromBitmap(rs , bmp);
        Allocation  output= Allocation.createTyped(rs , input.
                ↪→getType ());
        //3)  Creer le  scriptScript
        C_gray  grayScript = new  ScriptC_gray(rs);
        //4)  Copier  les  donnees  dans  les  Allocations//
        // ...
        // 5)  Initialiser  les  variables  globales  potentielles//
        // ...
        // 6)  Lancer  le noyau
        grayScript.forEach_pixelToGray(input , output);
        //7)  Recuperer  les  donnees  des  Allocation(s)
        output.copyTo(bmp);
        //8)  Detruire  le context , les  Allocation(s) et le  script
        input.destroy ();
        output.destroy ();
        grayScript.destroy ();
        rs.destroy ();
    }
    }*/

        int pixelToGray2(int pixel){
        int alpha = 0xFF << 24;
        int grey = pixel;

        int r = ((grey & 0x00FF0000) >> 16);
        int g = ((grey & 0x0000FF00) >> 8);
        int b = (grey & 0x000000FF);

        grey = (int) ((float) r * 0.3 + (float) g * 0.59 + (float) b * 0.11);
        grey = alpha | (grey << 16) | (grey << 8) | grey;
        return grey;

    }

    void toGray(Bitmap bmp){

        int width = bmp.getWidth();
        int height = bmp.getHeight();
        final int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for(int i=0;i<width;i++){
            for(int y = 0;y<height;y++){
                final int offset = y * width + i;
                pixels[offset] = pixelToGray2(pixels[offset]);
                //System.out.println("offest : "+offset+" gray : "+pixels[offset]);
            }
        }
        bmp.setPixels(pixels, 0, width, 0, 0, width, height);
    }


    int greyPixelToColor(int pixel,float h){
        int color ;
        int rgb[] ;

        float[] hsv = CustomColortoHSV( pixel);
        hsv[0]=h;
        rgb =CustomHSVtoRGB(hsv);
        color = Color.rgb(rgb[0],rgb[1],rgb[2]);
        return color;
    }

    public void colorize(Bitmap bmp){
        Random random = new Random();
        float h = 1 + (360 - 1) * random.nextFloat();
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        final int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for(int i=0;i<width;i++){
            for(int y = 0;y<height;y++){
                final int offset = y * width + i;
                pixels[offset] = greyPixelToColor(pixels[offset],h);
            }
        }
        bmp.setPixels(pixels, 0, width, 0, 0, width, height);
    }

    void toGrayExceptOneColor(Bitmap bmp,float c){
        float min = c-30;
        float max = c+30;

        int width = bmp.getWidth();
        int height = bmp.getHeight();
        final int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for(int i=0;i<width;i++){
            for(int y = 0;y<height;y++){
                final int offset = y * width + i;
                float[] hsv ;
                hsv=CustomColortoHSV(pixels[offset]);
                if(!(hsv[0]>min && hsv[0]<max)){
                    pixels[offset] = pixelToGray2(pixels[offset]);
                }
            }
        }
        bmp.setPixels(pixels, 0, width, 0, 0, width, height);
    }


    int[] returnHistotgram(Bitmap bmp){
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        final int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        int[] histo = new int[256];
        for (int i = 0; i < height; i++) {
            for (int y = 0; y < width; y++) {
                final int offset = y * width + i;
                histo[pixelToGray(pixels[offset])]++;
            }
        }
        return histo;
    }





    void ELDD(Bitmap bmp){

        int min=255;
        int max=0;
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        final int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for(int i=0;i<width;i++){
            for(int y = 0;y<height;y++){
                final int offset = y * width + i;
                int pixel = pixelToGray(pixels[offset]);
                if(pixel<min){

                    min=pixel;
                }else{
                    if(pixel>max){
                        max=pixel;
                    }
                }
            }

        }

        for(int i=0;i<width;i++){
            for(int y = 0;y<height;y++){
                final int offset = y * width + i;
                int I =pixelToGray(pixels[offset]);
                int newI =(255*(I-min))/(max-min);
               // System.out.println("offset = "+offset+" I: "+I+" newI : "+newI);
                pixels[offset]=Color.rgb(newI,newI,newI);
            }
        }
        bmp.setPixels(pixels, 0, width, 0, 0, width, height);
    }

    void DimCon(Bitmap bmp){

        int min=255;
        int max=0;
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        final int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for(int i=0;i<width;i++){
            for(int y = 0;y<height;y++){
                final int offset = y * width + i;
                int pixel = pixelToGray(pixels[offset]);
                if(pixel<min){

                    min=pixel;
                }else{
                    if(pixel>max){
                        max=pixel;
                    }
                }
            }

        }
        min+=30;
        max-=30;
        for(int i=0;i<width;i++){
            for(int y = 0;y<height;y++){
                final int offset = y * width + i;
                int I =pixelToGray(pixels[offset]);
                int newI =((255)*(I-min))/(max-min);
                //System.out.println("offset = "+offset+" I: "+I+" newI : "+newI);
                if(newI>0 && newI<255){
                    pixels[offset]=Color.rgb(newI,newI,newI);
                }else{
                    if(newI<30){
                        pixels[offset]=Color.rgb(30,30,30);
                    }
                    if(newI>225){
                        pixels[offset]=Color.rgb(225,225,225);
                    }
                }

            }
        }
        bmp.setPixels(pixels, 0, width, 0, 0, width, height);
    }



    void ELDDColor(Bitmap bmp){
        int value =100;
        double contrast = Math.pow((100 + value) / 100, 2);
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        final int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);

        for(int i=0;i<width;i++){
            for(int y = 0;y<height;y++){
                final int offset = y * width + i;
                int pixel = bmp.getPixel(i,y);
                int R =Color.red(pixel);
                int G =Color.green(pixel);
                int B =Color.blue(pixel);
                R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(R < 0) { R = 0; }
                else if(R > 255) { R = 255; }

                G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(G < 0) { G = 0; }
                else if(G > 255) { G = 255; }

                B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(B < 0) { B = 0; }
                else if(B > 255) { B = 255; }



                pixels[offset]=Color.rgb(R,G,B);

            }
        }
        bmp.setPixels(pixels, 0, width, 0, 0, width, height);

    }

    void EgalH(Bitmap bmp){
        int[] hist = returnHistotgram(bmp);
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        final int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);


        for(int i=1;i< 255;i++){
            hist[i]=hist[i-1]+hist[i];
        }
        for(int i=0;i<width;i++) {
            for (int y = 0; y < height; y++) {
                final int offset = y * width + i;
                int I =pixelToGray(pixels[offset]);
                int newI = (hist[I]*255)/(width*height);

                pixels[offset]=Color.rgb(newI,newI,newI);

            }
        }
        bmp.setPixels(pixels, 0, width, 0, 0, width, height);


    }


    int[] returnHistotgramColor(Bitmap bmp){
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int hist[] = new int[360];
        final int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for(int i=0;i<width;i++){
            for(int y = 0;y<height;y++){
                final int offset = y * width + i;
                float[] hsv = new float[3];
                Color.colorToHSV(pixels[offset],hsv);
                hist[(int)hsv[0]]++;
            }
        }
        return hist;
    }

    void EgalHColor(Bitmap bmp){
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        final int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        int[] hist =returnHistotgramColor(bmp);

        for(int i=1;i< 360;i++){
            hist[i]=hist[i-1]+hist[i];
        }
        for(int i=0;i<width;i++) {
            for (int y = 0; y < height; y++) {
                final int offset = y * width + i;

                float[] hsv = new float[3];
                Color.colorToHSV(pixels[offset],hsv);
                hsv[0]=(360*hist[(int)hsv[0]])/(width*height);
                int[] rgb =CustomHSVtoRGB(hsv);


                pixels[offset]=Color.rgb(rgb[0],rgb[1],rgb[2]);

            }
        }

        /*
        for(int i=0;i<width;i++) {
            for (int y = 0; y < height; y++) {
                final int offset = y * width + i;
                float[] hsv = new float[3];
                Color.colorToHSV(pixels[offset],hsv);
                hsv[0]=hist[(int)hsv[0]];
                int[] rgb =CustomHSVtoRGB(hsv);

                pixels[offset] = (360*rgb[0])/(width*height);
                pixels[offset]  = (pixels[offset]  << 8) + rgb[1];
                pixels[offset]  = (pixels[offset]  << 8) + rgb[2];
            }
        }*/
        bmp.setPixels(pixels, 0, width, 0, 0, width, height);

    }

    /*
    //Convolution


    int convolution3x3(Bitmap bmp,int i,int y){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        final int offset = y * w + i;
        int width=1;
        int height=1;
        int moyenne=0;
        final int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, i-width, y-width, (width*2)+1, (height*2)+1);
        for(int x=i-width;x<i+width;x++){
            for(int z=y-height;z<y+height;z++){
                int greyK = pixelToGray(pixels[offset]);
                moyenne = moyenne+(greyK/9);
            }
        }
        pixels[offset]=moyenne;

    }

    void convolution(Bitmap bmp){
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        final int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for(int i=0;i<width;i++) {
            for (int y = 0; y < height; y++) {
                final int offset = y * width + i;

            }
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.nanoTime();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;

        ImageView image = findViewById(R.id.imageView2);
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.image3);
        b = b.copy(Bitmap.Config.ARGB_8888,true);
        image.setImageBitmap(b);

        for(int i=0;i<b.getWidth();i++){
            b.setPixel(i, b.getHeight()/2, Color.rgb(1, 1, 1));
        }
        System.nanoTime();

    }

    public void onClickBtn(View v)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;

        ImageView image = (ImageView) findViewById(R.id.imageView2);
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.image3);
        b = b.copy(Bitmap.Config.ARGB_8888,true);
        image.setImageBitmap(b);
        toGray(b);
    }

    public void onClickBtnColor(View v)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;

        ImageView image = (ImageView) findViewById(R.id.imageView2);
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.image3);
        b = b.copy(Bitmap.Config.ARGB_8888,true);
        image.setImageBitmap(b);
        colorize(b);
    }

    public void onClickBtntoGrayExcept(View v)
    {
        Random random = new Random();
        int h = random.nextInt(360) ;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;

        ImageView image = (ImageView) findViewById(R.id.imageView2);
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.image3);
        b = b.copy(Bitmap.Config.ARGB_8888,true);
        image.setImageBitmap(b);
        toGrayExceptOneColor(b,h);
    }

    public void onClickBtntELDD(View v)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        ImageView image = (ImageView) findViewById(R.id.imageView);
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.image2);
        b = b.copy(Bitmap.Config.ARGB_8888,true);
        image.setImageBitmap(b);
        ELDD(b);
    }

    public void onClickBtntELDDColor(View v)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        ImageView image = (ImageView) findViewById(R.id.imageView2);
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.image3);
        b = b.copy(Bitmap.Config.ARGB_8888,true);
        image.setImageBitmap(b);
        ELDDColor(b);
    }

    public void onClickBtntDimCon(View v)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        ImageView image = (ImageView) findViewById(R.id.imageView4);
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.image5);
        b = b.copy(Bitmap.Config.ARGB_8888,true);
        image.setImageBitmap(b);
        DimCon(b);
    }

    public void onClickButtonEgalH(View v)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        ImageView image = (ImageView) findViewById(R.id.imageView);
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.image2);
        b = b.copy(Bitmap.Config.ARGB_8888,true);
        image.setImageBitmap(b);
        EgalH(b);
    }

    public void onClickButtonEgalHColor(View v)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        ImageView image = (ImageView) findViewById(R.id.imageView2);
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.image3);
        b = b.copy(Bitmap.Config.ARGB_8888,true);
        image.setImageBitmap(b);
        EgalHColor(b);
    }

    public void onClickButtonTest(View v)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        ImageView image = (ImageView) findViewById(R.id.imageView);
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.image2);
        b = b.copy(Bitmap.Config.ARGB_8888,true);
        image.setImageBitmap(b);
        int[] hist =returnHistotgram(b);
    }

    public void onClickButtonReset(View v)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;

        ImageView image = (ImageView) findViewById(R.id.imageView);
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.image2);
        b = b.copy(Bitmap.Config.ARGB_8888,true);
        image.setImageBitmap(b);

        ImageView image1 = (ImageView) findViewById(R.id.imageView2);
        Bitmap b1 = BitmapFactory.decodeResource(getResources(), R.drawable.image3);
        b1 = b1.copy(Bitmap.Config.ARGB_8888,true);
        image1.setImageBitmap(b1);

        ImageView image2 = (ImageView) findViewById(R.id.imageView4);
        Bitmap b2 = BitmapFactory.decodeResource(getResources(), R.drawable.image5);
        b2 = b2.copy(Bitmap.Config.ARGB_8888,true);
        image2.setImageBitmap(b2);

    }






}