package bgu.spl.net.impl.echo;

import bgu.spl.net.api.EncoderDecoderSTOMP;
import bgu.spl.net.srv.Frame;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class EchoClient {

    private static EncoderDecoderSTOMP encdec = new EncoderDecoderSTOMP();

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            args = new String[]{"localhost",
                    "CONNECT\n" +
                            "accept-version:1.2\n" +
                            "host:stomp.cs.bgu.ac.il\n" +
                            "login:bob\n" +
                            "passcode:alice\n" +
                            " \n" +
                            "\u0000",
                    "SUBSCRIBE\n" +
                            "destination:sci-fi\n" +
                            "id:78\n" +
                            "receipt:77\n" +
                            " \n" +
                            "\u0000",
                    "SEND\n" +
                    "destination:sci-fi\n" +
                            " \n" +
            "Bob has added the book Foundation\n" +
            "\u0000"};
        }

        if (args.length < 2) {
            System.out.println("you must supply two arguments: host, message");
            System.exit(1);
        }

        //BufferedReader and BufferedWriter automatically using UTF-8 encoding
        try (Socket sock = new Socket(args[0], 7777);
             BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
             PrintWriter out = new PrintWriter(sock.getOutputStream())) {

            System.out.println("sending message to server");
            out.write(args[1]);
            out.flush();

            int read;
            while ((read = in.read()) >= 0) {
                Frame nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    System.out.println(nextMessage.getStompCommand());
                    System.out.println(nextMessage.getHeaders());
                    System.out.println(nextMessage.getFrameBody() + "\n" + "^@");
                    break;
                }
            }

            System.out.println("sending message to server");
            out.write(args[2]);
            out.flush();

            while ((read = in.read()) >= 0) {
                Frame nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    System.out.println(nextMessage.getStompCommand());
                    System.out.println(nextMessage.getHeaders());
                    System.out.println(nextMessage.getFrameBody() + "\n" + "^@");
                    break;
                }
            }

            System.out.println("sending message to server");
            out.write(args[3]);
            out.flush();

            System.out.println("awaiting response");
            System.out.println("message from server: \n");

            while ((read = in.read()) >= 0) {
                Frame nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    System.out.println(nextMessage.getStompCommand());
                    System.out.println(nextMessage.getHeaders());
                    System.out.println(nextMessage.getFrameBody() + "\n" + "^@");
                    break;
                }
            }
        }
    }
}
