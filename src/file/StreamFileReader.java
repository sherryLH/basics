package file;
/**
 * 文件字节流
 */

import com.sun.management.OperatingSystemMXBean;
import com.sun.management.UnixOperatingSystemMXBean;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public class StreamFileReader {
    private BufferedInputStream bufferedInputStream;
    private long fileLength;
    private int arraySize;
    private byte[] array;

    public StreamFileReader(String filePath, int arraySize) throws IOException{
        FileInputStream fileInputStream = new FileInputStream(filePath);
        this.bufferedInputStream = new BufferedInputStream(fileInputStream, arraySize);
        this.fileLength = bufferedInputStream.available();
        this.arraySize = arraySize;
    }

    public int read() throws IOException{
        byte[] tmpArray = new byte[arraySize];
        int bytes = bufferedInputStream.read(tmpArray);
        if (bytes != -1){
            array = new byte[bytes];
            System.arraycopy(tmpArray,0,array,0,bytes);
            OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
            System.out.println(operatingSystemMXBean.getFreeSwapSpaceSize());
            return bytes;
        }
        return -1;
    }

    public void close() throws IOException{
        bufferedInputStream.close();
        array = null;
    }

    public long getFileLength() {
        return fileLength;
    }

    public byte[] getArray() {
        return array;
    }

    public static void main(String[] args) throws IOException{
        StreamFileReader streamFileReader = new StreamFileReader("D:\\BaiduNetdiskDownload\\云之彼端.mp4",65536);//1024*64
        long start = System.nanoTime();
        while (streamFileReader.read() != -1);
        long end = System.nanoTime();
        streamFileReader.close();
        System.out.println("StreamFileReader:"+(end - start));
    }
}
