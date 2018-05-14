package file;
/**
 * 文件通道
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ChannelFileReader {
    private FileInputStream fileInputStream;
    private ByteBuffer byteBuffer;//nio通道
    private long fileLength;
    private int arraySize;
    private byte[] array;

    public ChannelFileReader(String filePath, int arraySize) throws IOException{
        this.fileInputStream = new FileInputStream(filePath);
        this.fileLength = fileInputStream.getChannel().size();
        this.arraySize = arraySize;
        this.byteBuffer = ByteBuffer.allocate(arraySize);
    }

    public int read() throws IOException{
        FileChannel fileChannel = fileInputStream.getChannel();
        int bytes = fileChannel.read(byteBuffer);//读取到nio通道的byteBuffer中
        if (bytes != -1){
            array = new byte[bytes];//字节数组长度为已读长度
            byteBuffer.flip();
            byteBuffer.get(array);//从byteBuffer中得到字节数组
            byteBuffer.clear();
            return bytes;
        }
        return -1;
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
        ChannelFileReader channelFileReader = new ChannelFileReader("D:\\BaiduNetdiskDownload\\云之彼端.mp4", 65536);
        long start = System.nanoTime();
        while(channelFileReader.read() != -1);
        long end = System.nanoTime();
        channelFileReader.close();
        System.out.println("ChannelFileReader:"+(end-start));
    }
}
