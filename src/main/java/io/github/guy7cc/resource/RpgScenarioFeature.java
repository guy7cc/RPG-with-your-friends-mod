package io.github.guy7cc.resource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.apache.commons.lang3.NotImplementedException;

public abstract class RpgScenarioFeature {
    public static final Codec<RpgScenarioFeature> CODEC = CodecUtil.toParentCodec(RpgScenarioFeature.class,
            new CodecUtil.WithType<>(Adventure.CODEC, Adventure.class),
            new CodecUtil.WithType<>(KeepInventory.CODEC, KeepInventory.class)
    );

    public abstract void apply();

    public abstract void render();

    public static class Adventure extends RpgScenarioFeature {
        public static final Codec<Adventure> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.fieldOf("adventure").forGetter(f -> f.adventure)
        ).apply(instance, Adventure::new));

        private boolean adventure;

        public Adventure(boolean adventure){
            this.adventure = adventure;
        }

        @Override
        public void apply() {
            throw new NotImplementedException();
        }

        @Override
        public void render(){
            throw new NotImplementedException();
        }
    }

    public static class KeepInventory extends RpgScenarioFeature {
        public static final Codec<KeepInventory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.fieldOf("keepInventory").forGetter(f -> f.keepInventory)
        ).apply(instance, KeepInventory::new));

        private boolean keepInventory;

        public KeepInventory(boolean keepInventory){
            this.keepInventory = keepInventory;
        }

        @Override
        public void apply() {
            throw new NotImplementedException();
        }

        @Override
        public void render(){
            throw new NotImplementedException();
        }
    }
}
