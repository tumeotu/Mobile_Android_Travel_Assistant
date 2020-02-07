package com.ygaps.travelapp.Component;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Image
{
    String avatar;
    Bitmap image;
    Uri uri;

    public Image(Uri uri)
    {
        this.uri = uri;
        uriToBitMap();
        this.avatar = BitMapToString();
    }

    public Image(Bitmap image)
    {
        this.image = image;
        this.avatar = BitMapToString();
    }

    public Image(String avatar)
    {
        this.avatar = avatar;
        this.image = StringToBitMap();
    }

    public Uri getUri()
    {
        return uri;
    }

    public void setUri(Uri uri)
    {
        this.uri = uri;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public Bitmap getImage()
    {
        return image;
    }

    public void setImage(Bitmap image)
    {
        this.image = image;
    }

    public String BitMapToString()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public Bitmap StringToBitMap()
    {
        try
        {
            byte[] encodeByte = Base64.decode(this.avatar, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        }
        catch (Exception e)
        {
            e.getMessage();
            return null;
        }
    }

    public Bitmap uriToBitMap()
    {
        InputStream is = null;
        BufferedInputStream bis = null;
        try
        {
            URLConnection conn = new URL(this.uri.toString()).openConnection();
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is, 8192);
            this.image = BitmapFactory.decodeStream(bis);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (bis != null)
            {
                try
                {
                    bis.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return this.image;
    }
}
