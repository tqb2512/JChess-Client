package model.piece;

import lombok.Getter;
import lombok.Setter;
import model.Board;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;

@Getter
@Setter
public abstract class Piece {
    private final String name;
    private final String color;
    public BufferedImage image;

    public Piece(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public abstract ArrayList<int[][]> getValidMoves(int currentCol, int currentRow, Board board);

    public BufferedImage loadSVGImage(String path) {
        try {
            PNGTranscoder transcoder = new PNGTranscoder();
            transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, 70f);
            transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, 70f);
            TranscoderInput input = new TranscoderInput(getClass().getResourceAsStream(path));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(stream);
            transcoder.transcode(input, output);
            return ImageIO.read(new ByteArrayInputStream(stream.toByteArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
