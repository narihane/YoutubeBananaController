package Controller;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

import java.util.HashSet;
import java.util.Scanner;

public class Controller {

    public static final HashSet<ChannelHandlerContext> channels = new HashSet<>();
    public Controller() throws InterruptedException {

    }

    public void run() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();

        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new ControllerInitializer());

            ChannelFuture channelFuture = serverBootstrap.bind(9999).sync();

            //any code related to server channel should be before the closeFuture

            Thread one = new Thread() {
                public void run() {
                    try {
                        while (true){
                            Scanner sc = new Scanner(System.in);
                            String line = sc.nextLine();
                            System.out.println(Controller.channels.size());
                            for(ChannelHandlerContext c : channels)
                                c.channel().writeAndFlush(Unpooled.copiedBuffer(line, CharsetUtil.UTF_8));
                        }
                    } catch(Exception v) {
                        System.out.println(v);
                    }
                }
            };

            one.start();

            channelFuture.channel().closeFuture().sync();
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        Controller c = new Controller();
        c.run();
    }
}