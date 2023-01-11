package io.github.guy7cc.resource;

import com.mojang.serialization.Codec;

public class CodecWithType<T> {
    public Codec<T> codec;
    public Class<T> type;

    public CodecWithType(Codec<T> codec, Class<T> type){
        this.codec = codec;
        this.type = type;
    }
}
