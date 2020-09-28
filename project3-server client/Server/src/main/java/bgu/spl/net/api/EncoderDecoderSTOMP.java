package bgu.spl.net.api;

import bgu.spl.net.srv.Frame;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EncoderDecoderSTOMP implements MessageEncoderDecoder<Frame> {

    private byte[] bytes = new byte[1 << 10];
    private int length = 0;

    @Override
    public Frame decodeNextByte(byte nextByte) {
        if (nextByte == '\u0000')
            return popFrame();
        pushByte(nextByte);
        return null;
    }

    @Override
    public byte[] encode(Frame message) {
        String s = message.getStompCommand() + "\n";
        Map<String, String> headers = message.getHeaders();
        for (Map.Entry<String,String> header : headers.entrySet())
            s += header.getKey() + ":" + header.getValue() + "\n";
        s += "\n" + message.getFrameBody();
        s += '\n';
        s += '\u0000';
        return s.getBytes();
    }

    private void pushByte(byte nextByte) {
        if (length >= bytes.length)
            bytes = Arrays.copyOf(bytes, length * 2);
        bytes[length++] = nextByte;
    }

    private Frame popFrame() {
        String result = new String(bytes, 0, length, StandardCharsets.UTF_8);
        String[] lines = result.split("\r\n|\r|\n");
        String stompCommand = lines[0];
        Map<String, String> headers = new HashMap<>();
        int index = 1;
        while (index < lines.length && !(lines[index].length() == 0)) {
            String headerKey = lines[index].substring(0, lines[index].indexOf(":"));
            String headerValue = lines[index].substring(lines[index].indexOf(":") + 1);
            headers.put(headerKey, headerValue);
            index++;
        }
        String frameBody = "";
        index++;
        while (index < lines.length) {
            frameBody += lines[index] + "\n";
            index++;
        }
        length = 0;
        Frame frame = new Frame(stompCommand, headers, frameBody);
        return frame;
    }

}
