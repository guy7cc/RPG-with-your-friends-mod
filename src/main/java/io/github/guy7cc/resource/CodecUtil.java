package io.github.guy7cc.resource;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class CodecUtil {
    public static <P, C extends P> Codec<P> toParentCodec(CodecWithType<C> codec){
        return codec.codec.flatComapMap(
                Function.identity(),
                parent -> {
                    if(codec.type.isAssignableFrom(parent.getClass())) return DataResult.success((C) parent);
                    else return DataResult.error("The object is not " + codec.type.getCanonicalName());
                });
    }

    public static <P, C1 extends P, C2 extends P> Codec<P> toParentCodec(CodecWithType<C1> codec1, CodecWithType<C2> codec2){
        return Codec.either(codec1.codec, codec2.codec).flatComapMap(
                either -> either.map(Function.identity(), Function.identity()),
                parent -> {
                    if(codec1.type.isAssignableFrom(parent.getClass())) return DataResult.success(Either.left((C1) parent));
                    else if(codec2.type.isAssignableFrom(parent.getClass())) return DataResult.success(Either.right((C2) parent));
                    else return DataResult.error("The object is neither " + codec1.type.getCanonicalName() + " nor " + codec2.type.getCanonicalName());
                });
    }

    public static <T> Codec<T> toParentCodec(Class<T> type, CodecWithType<? extends T>... codecs){
        return toParentCodec(type, Arrays.stream(codecs).toList());
    }

    public static <T> Codec<T> toParentCodec(Class<T> type, List<CodecWithType<? extends T>> codecs){
        if(codecs.size() == 0) return null;
        else if(codecs.size() == 1) {
            return toParentCodec(codecs.get(0));
        } else if(codecs.size() == 2) {
            return toParentCodec(codecs.get(0), codecs.get(1));
        } else {
            List<CodecWithType<? extends T>> remainingCodecs = codecs.subList(2, codecs.size());
            CodecWithType<T> partialParentCodec = new CodecWithType<>(toParentCodec(codecs.get(0), codecs.get(1)), type);
            return appendChildrenCodec(partialParentCodec, remainingCodecs);
        }
    }

    public static <T> Codec<T> appendChildrenCodec(CodecWithType<T> parentCodec, List<CodecWithType<? extends T>> codecs){
        if(codecs.size() == 0) return parentCodec.codec;
        else {
            List<CodecWithType<? extends T>> remainingCodecs = codecs.subList(1, codecs.size());
            CodecWithType<T> partialParentCodec = new CodecWithType<>(toParentCodec(parentCodec, codecs.get(0)), parentCodec.type);
            return appendChildrenCodec(partialParentCodec, remainingCodecs);
        }
    }



}
