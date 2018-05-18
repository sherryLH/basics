package file;
/**
 * 内存映射数组
 */

import com.sun.management.OperatingSystemMXBean;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MappedBiggerFileReader {
    private MappedByteBuffer[] mappedByteBuffers;
    private int count =0;
    private int number;
    private FileInputStream fileInputStream;
    private long fileLength;
    private int arraySize;
    private byte[] array;

    public MappedBiggerFileReader(String filePath, int arraySize) throws IOException{
        this.fileInputStream = new FileInputStream(filePath);
        FileChannel fileChannel = fileInputStream.getChannel();
        this.fileLength = fileChannel.size();
        this.number = (int)Math.ceil((double)fileLength/(double)Integer.MAX_VALUE);
        this.mappedByteBuffers = new MappedByteBuffer[number];//内存文件映射数组
        long preLength = 0;
        long regionSize = (long)Integer.MAX_VALUE;//映射区域大小
        for(int i = 0; i < number; i ++){
            if(fileLength - preLength < (long)Integer.MAX_VALUE){
                regionSize = fileLength - preLength;//最后一片区域的大小
            }
            mappedByteBuffers[i] = fileChannel.map(FileChannel.MapMode.READ_ONLY,preLength,regionSize);
            preLength += regionSize;
        }
        this.arraySize = arraySize;
    }

    public int read() throws IOException{
        if(count >= number)
            return -1;
        int limit = mappedByteBuffers[count].limit();
        int position = mappedByteBuffers[count].position();
        if(limit - position > arraySize){
            array = new byte[arraySize];
            mappedByteBuffers[count].get(array);
            OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
            System.out.println(operatingSystemMXBean.getFreeSwapSpaceSize());
            return arraySize;
        }else{
            array = new byte[limit - position];
            mappedByteBuffers[count].get(array);
            if(count < number){
                count ++;
            }
            OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
            System.out.println(operatingSystemMXBean.getFreeSwapSpaceSize());
            return limit - position;
        }
    }

    public void close() throws IOException{
        fileInputStream.close();
        array = null;
    }

    public long getFileLength() {
        return fileLength;
    }

    public byte[] getArray() {
        return array;
    }

    public static void main(String[] args) throws IOException{
        MappedBiggerFileReader mappedBiggerFileReader = new MappedBiggerFileReader("D:\\BaiduNetdiskDownload\\云之彼端.mp4", 655369);
        long start = System.nanoTime();
        while (mappedBiggerFileReader.read() != -1);
        long end = System.nanoTime();
        mappedBiggerFileReader.close();
        System.out.println("MappedBiggerFileReader:"+(end - start));
    }
}
