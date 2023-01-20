package io.github.guy7cc.resource;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class CodecUtil {
    public static <P, C extends P> Codec<P> toParentCodec(WithType<C> codec){
        return codec.codec.flatComapMap(
                Function.identity(),
                parent -> {
                    if(codec.type.isAssignableFrom(parent.getClass())) return DataResult.success((C) parent);
                    else return DataResult.error("The object is not " + codec.type.getCanonicalName());
                });
    }

    public static <P, C1 extends P, C2 extends P> Codec<P> toParentCodec(WithType<C1> codec1, WithType<C2> codec2){
        return Codec.either(codec1.codec, codec2.codec).flatComapMap(
                either -> either.map(Function.identity(), Function.identity()),
                parent -> {
                    if(codec1.type.isAssignableFrom(parent.getClass())) return DataResult.success(Either.left((C1) parent));
                    else if(codec2.type.isAssignableFrom(parent.getClass())) return DataResult.success(Either.right((C2) parent));
                    else return DataResult.error("The object is neither " + codec1.type.getCanonicalName() + " nor " + codec2.type.getCanonicalName());
                });
    }

    public static <T> Codec<T> toParentCodec(Class<T> type, WithType<? extends T>... codecs){
        return toParentCodec(type, Arrays.stream(codecs).toList());
    }

    public static <T> Codec<T> toParentCodec(Class<T> type, List<WithType<? extends T>> codecs){
        if(codecs.size() == 0) return null;
        else if(codecs.size() == 1) {
            return toParentCodec(codecs.get(0));
        } else if(codecs.size() == 2) {
            return toParentCodec(codecs.get(1), codecs.get(0));
        } else {
            List<WithType<? extends T>> remainingCodecs = codecs.subList(2, codecs.size());
            WithType<T> partialParentCodec = new WithType<>(toParentCodec(codecs.get(1), codecs.get(0)), type);
            return appendChildrenCodec(partialParentCodec, remainingCodecs);
        }
    }

    public static <T> Codec<T> appendChildrenCodec(WithType<T> parentCodec, List<WithType<? extends T>> codecs){
        if(codecs.size() == 0) return parentCodec.codec;
        else {
            List<WithType<? extends T>> remainingCodecs = codecs.subList(1, codecs.size());
            WithType<T> partialParentCodec = new WithType<>(toParentCodec(codecs.get(0), parentCodec), parentCodec.type);
            return appendChildrenCodec(partialParentCodec, remainingCodecs);
        }
    }


    public static class WithType<T> {
        public Codec<T> codec;
        public Class<T> type;

        public WithType(Codec<T> codec, Class<T> type){
            this.codec = codec;
            this.type = type;
        }
    }
}
