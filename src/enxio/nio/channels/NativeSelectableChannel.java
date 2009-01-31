package enxio.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;

public class NativeSelectableChannel extends AbstractSelectableChannel
        implements ByteChannel {
    
    private final int fd;
    private final int validOps;

    public NativeSelectableChannel(SelectorProvider provider, int fd, int ops) {
        super(provider);
        this.fd = fd;
        this.validOps = ops;
    }
    public static NativeSelectableChannel forSocket(int fd) {
        return new NativeSelectableChannel(NativeSelectorProvider.getInstance(), fd,
                SelectionKey.OP_READ | SelectionKey.OP_WRITE | SelectionKey.OP_CONNECT | SelectionKey.OP_ACCEPT);
    }
    public static NativeSelectableChannel forServerSocket(int fd) {
        return forSocket(fd);
    }
    public static NativeSelectableChannel forDevice(int fd) {
        return new NativeSelectableChannel(NativeSelectorProvider.getInstance(), fd,
                SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }
    
    @Override
    protected void implCloseSelectableChannel() throws IOException {
       Native.close(fd);
    }

    @Override
    protected void implConfigureBlocking(boolean block) throws IOException {
        Native.setBlocking(fd, block);
    }

    @Override
    public final int validOps() {
        return validOps;
    }
    public final int getFD() {
        return fd;
    }
    public int read(ByteBuffer dst) throws IOException {
        return Native.read(fd, dst);
    }

    public int write(ByteBuffer src) throws IOException {
        return Native.write(fd, src);
    }
}