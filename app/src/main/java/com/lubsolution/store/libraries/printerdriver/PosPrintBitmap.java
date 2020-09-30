package com.lubsolution.store.libraries.printerdriver;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.lubsolution.store.callback.CallbackBoolean;
import com.lubsolution.store.models.Printer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


/**
 * Created by tranhuy on 2/14/17.
 */
public class PosPrintBitmap extends AsyncTask<Printer, Void, Boolean> {
    public CallbackBoolean delegate = null;
    Context context;
    Bitmap bitmap;
    private final static char ESC_CHAR = 0x1B;

    public PosPrintBitmap(Context context, Bitmap bitmap, CallbackBoolean asyncResponse) {
        delegate = asyncResponse;//Assigning call back interfacethrough constructor
        this.context = context;
        this.bitmap = bitmap;
    }

    @Override
    protected Boolean doInBackground(Printer... params) {
        Socket client;
        OutputStream out = null;
        InputStream inStream = null;
        try {
            InetAddress e = InetAddress.getByName(params[0].getIpAdress());
            SocketAddress sockaddr = new InetSocketAddress(e, params[0].getPort());
            client = new Socket();
            client.connect(sockaddr, 5000);

            if (client != null) {
                out = client.getOutputStream();
                inStream = client.getInputStream();
            }
            //Toan code here

            int[][] pixels = getPixelsSlow(bitmap);
            printImage(bitmap, out);

            //close connection
            if (client != null) {
                client.close();
                out = null;
                inStream = null;
                client = null;
            }

        } catch (Exception c) {
            //c.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        delegate.onRespone(aBoolean);
    }

    private static int[][] getPixelsSlow(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] result = new int[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                result[row][col] = image.getPixel(col, row);
            }
        }
        return result;
    }

    private static void printImage(Bitmap bitmap, OutputStream printPort) throws IOException {
        //setup printer
        byte[] INIT_PRINTER = new byte[]{ESC_CHAR, 0x40};
        byte[] LINE_FEED = new byte[]{0x0A};
        byte[] PAPER_CUT = new byte[]{0x1B, 0x40, 0x1D, 0x56, 0x01};
        byte[] sendData = null;
        LinePrinter pg = new LinePrinter();
        int i = 0, s = 0, j = 0, index = 0, lines = 0;
        pg.initCanvas(bitmap.getWidth());
        pg.initPaint();
        pg.drawImage(0, 0, bitmap);
        //pg.printBitmap(bitmap);
        sendData = pg.printDraw();
        byte[] temp = new byte[(pg.getWidth() / 8) * 5];
        byte[] dHeader = new byte[8];
        if (pg.getLength() != 0) {
            dHeader[0] = 0x1D;
            dHeader[1] = 0x76;
            dHeader[2] = 0x30;
            dHeader[4] = (byte) (pg.getWidth() / 8);
            dHeader[5] = 0x00;
            dHeader[6] = (byte) (pg.getLength() % 256);
            dHeader[7] = (byte) (pg.getLength() / 256);
            printPort.write(INIT_PRINTER);
            printPort.write(dHeader);
            for (i = 0; i < (pg.getLength() / 5) + 1; i++) {
                s = 0;
                if (i < pg.getLength() / 5) {
                    lines = 5;
                } else {
                    lines = pg.getLength() % 5;
                }
                for (j = 0; j < lines * (pg.getWidth() / 8); j++) {
                    temp[s++] = sendData[index++];
                }
                printPort.write(temp);
                for (j = 0; j < (pg.getWidth() / 8) * 5; j++) {
                    temp[j] = 0;
                }
            }
        }
        // Do a line feed, if not the printing will resume on the same line
        //printPort.write(LINE_FEED);
        //printPort.write(LINE_FEED);
        //printPort.write(LINE_FEED);
        //printPort.write(LINE_FEED);
        printPort.write(LINE_FEED);
        printPort.write(LINE_FEED);
        printPort.write(LINE_FEED);
        printPort.write(LINE_FEED);
        printPort.write(PAPER_CUT);

    }
}
