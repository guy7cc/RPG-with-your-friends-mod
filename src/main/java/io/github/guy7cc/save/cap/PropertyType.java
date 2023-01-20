package io.github.guy7cc.save.cap;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class PropertyType<T> {
    public static final PropertyType<Float> MP = new PropertyType<>(1, p -> p.mp, (p, mp) -> p.setMp(mp), (p, buf) -> buf.writeFloat(p.mp), (p, buf) -> p.mp = buf.readFloat());
    public static final PropertyType<Float> MAX_MP = new PropertyType<>(2, p -> p.maxMp, (p, maxMp) -> p.setMaxMp(maxMp), (p, buf) -> buf.writeFloat(p.maxMp), (p, buf) -> p.maxMp = buf.readFloat());
    public static final PropertyType<Long> MONEY = new PropertyType<>(4, p -> p.money, (p, money) -> p.setMoney(money), (p, buf) -> buf.writeLong(p.money), (p, buf) -> p.money = buf.readLong());
    public static final PropertyType<Long> MAX_MONEY = new PropertyType<>(8, p -> p.maxMoney, (p, maxMoney) -> p.setMaxMoney(maxMoney), (p, buf) -> buf.writeLong(p.maxMoney), (p, buf) -> p.maxMoney = buf.readLong());
    public static final ImmutableList<PropertyType<?>> allTypes = ImmutableList.of(MP, MAX_MP, MONEY, MAX_MONEY);

    protected final int bitMask;
    private final Function<RpgPlayerProperty, T> getter;
    private final BiFunction<RpgPlayerProperty, T, Boolean> setter;
    private final BiConsumer<RpgPlayerProperty, FriendlyByteBuf> writer;
    private final BiConsumer<RpgPlayerProperty, FriendlyByteBuf> reader;

    private PropertyType(int bitMask, Function<RpgPlayerProperty, T> getter, BiFunction<RpgPlayerProperty, T, Boolean> setter, BiConsumer<RpgPlayerProperty, FriendlyByteBuf> writer, BiConsumer<RpgPlayerProperty, FriendlyByteBuf> reader) {
        this.bitMask = bitMask;
        this.getter = getter;
        this.setter = setter;
        this.writer = writer;
        this.reader = reader;
    }

    protected T get(RpgPlayerProperty p) {
        return getter.apply(p);
    }

    protected void set(RpgPlayerProperty p, T value) {
        if (setter.apply(p, value)) {
            p.dirty |= bitMask;
        }
    }

    protected void write(RpgPlayerProperty p, FriendlyByteBuf buf) {
        writer.accept(p, buf);
    }

    protected void read(RpgPlayerProperty p, FriendlyByteBuf buf) {
        reader.accept(p, buf);
    }
}
