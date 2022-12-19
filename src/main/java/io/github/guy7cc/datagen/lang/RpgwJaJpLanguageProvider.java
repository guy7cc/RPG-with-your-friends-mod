package io.github.guy7cc.datagen.lang;

import io.github.guy7cc.block.RpgwBlocks;
import io.github.guy7cc.item.RpgwItems;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class RpgwJaJpLanguageProvider extends RpgwAbstractLanguageProvider {
    public RpgwJaJpLanguageProvider(DataGenerator gen, String modId) {
        super(gen, modId, "ja_jp");
    }

    @Override
    protected void addTranslations() {
        addItemGroup("rpgwmod", "RPG with your friends Mod");

        add(RpgwBlocks.EXAMPLE_BORDER.get(), "参考用境界ブロック");
        add(RpgwBlocks.RPGW_SPAWNER.get(), "RPGスポナー");
        add(RpgwBlocks.BORDERED_RPGW_SPAWNER.get(), "境界付きRPGスポナー");
        add(RpgwBlocks.VENDING_MACHINE.get(), "自動販売機");

        add(RpgwItems.BORDER_WRENCH.get(), "レンチ（ボーダー用）");
        add(RpgwItems.SPAWNER_WRENCH.get(), "レンチ（スポナー用）");
        add(RpgwItems.IRON_COIN.get(), "硬貨 鉄");
        add(RpgwItems.COPPER_COIN.get(), "硬貨 銅");
        add(RpgwItems.SILVER_COIN.get(), "硬貨 銀");
        add(RpgwItems.GOLD_COIN.get(), "硬貨 金");

        String prefix = "partyMenu.";
        addGui("partyMenu", "パーティメニュー");
        addGui(prefix + "currentParty", "所属パーティ");
        addGui(prefix + "noCurrentParty", "なし");
        addGui(prefix + "joinRequest", "参加リクエスト");
        addGui(prefix + "joinRequestCheck", "%sから参加リクエストを受けています。");
        addGui(prefix + "joinRequestAccept", "許可");
        addGui(prefix + "joinRequestDeny", "拒否");
        addGui(prefix + "joinRequestFail", "参加リクエストの送信に失敗しました。");
        addGui(prefix + "changeLeader", "リーダー変更");
        addGui(prefix + "cannotChangeLeader", "あなたはリーダーではありません。");
        addGui(prefix + "leave", "脱退");
        addGui(prefix + "cannotLeave", "今はパーティを抜けることができません。");
        addGui(prefix + "someoneLeave", "%sがパーティを脱退しました。");

        prefix = "createPartyMenu.";
        addGui("createPartyMenu", "パーティ名");
        addGui(prefix + "create", "作成");
        addGui(prefix + "cancel", "キャンセル");
        addGui(prefix + "cannotCreate", "新しいパーティを作るためには、今のパーティを抜ける必要があります。");

        prefix = "editScreen.";
        addGui(prefix + "min.x", "Minimum X");
        addGui(prefix + "min.y", "Minimum Y");
        addGui(prefix + "min.z", "Minimum Z");
        addGui(prefix + "max.x", "Maximum X");
        addGui(prefix + "max.y", "Maximum Y");
        addGui(prefix + "max.z", "Maximum Z");
        addGui(prefix + "invalid", "Cannot save invalid values.");
        addGui(prefix + "spawnArea", "Spawn");
        addGui(prefix + "playerArea", "Player");
        addGui(prefix + "delay", "Delay");

        prefix = "trader.";
        addGui(prefix + "buy", "買う");
        addGui(prefix + "sell", "売る");
        addGui(prefix + "barter", "交換");
        addGui(prefix + "upto", "%s 個まで");
        addGui(prefix + "available", "あと %s");
        addGui(prefix + "needs", "必要数: %s");
        addGui(prefix + "confirm", "確定");

        String command = "rpgw";
        addCommands(command, "jreqAccepted", "参加リクエストは受理されました。");
        addCommands(command, "jreqDenied", "参加リクエストは却下されました。");
        addCommands(command, "jreqFail", "参加リクエストの処理に失敗しました。");
    }
}