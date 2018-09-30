package bo.roman.radio.ui.model;

import bo.roman.radio.player.model.Codec;

public class CodecInformation {

	private final Codec codec;

	public CodecInformation(Codec codec) {
		this.codec = codec;
	}

	public Codec getCodec() {
		return codec;
	}
}
