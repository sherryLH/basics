package file;
/**
 * 内存文件映射
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MappedFileReader {
    private FileInputStream fileInputStream;
    private MappedByteBuffer mappedBuf;
    private long fileLength;
    private int arraySize;
    private byte[] array;

    public MappedFileReader(String filePath, int arraySize) throws IOException{
        this.fileInputStream = new FileInputStream(filePath);
        FileChannel fileChannel = fileInputStream.getChannel();
        this.fileLength = fileChannel.size();
        this.mappedBuf = fileChannel.map(FileChannel.MapMode.READ_ONLY,0,fileLength);
        this.arraySize = arraySize;
    }

    public int read() throws IOException{
        int limit = mappedBuf.limit();
        int position = mappedBuf.position();
        if(position == limit)
            return -1;
        if(limit - position > arraySize){
            array = new byte[arraySize];
            mappedBuf.get(array);
            return arraySize;
        }else{//读取剩余的数据
            array = new byte[limit-position];
            mappedBuf.get(array);
            return limit-position;
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
        MappedFileReader mappedFileReader = new MappedFileReader("D:\\BaiduNetdiskDownload\\云之彼端.mp4",65536);
        long start = System.nanoTime();
        while(mappedFileReader.read() != -1);
        long end = System.nanoTime();
        System.out.println("MappedFileReader:"+(end - start));
    }
}
