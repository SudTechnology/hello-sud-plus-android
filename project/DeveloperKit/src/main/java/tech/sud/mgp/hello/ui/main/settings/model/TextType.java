package tech.sud.mgp.hello.ui.main.settings.model;

public enum TextType {
    TextRow(0),         // 文本带箭头layout布局
    TextPlain(1),       // 文本不带箭头
    TickPlain(2),       // 打勾样式
    LogoText(3);      // 前面logo,后面文字

    private int value = 0;
    TextType(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
