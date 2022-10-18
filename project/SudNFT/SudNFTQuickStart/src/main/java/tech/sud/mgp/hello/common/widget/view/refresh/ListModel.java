package tech.sud.mgp.hello.common.widget.view.refresh;

import java.util.List;

public class ListModel<T> {
    public int pageNumber;
    public int pageSize;
    public List<T> datas;

    public ListModel() {
    }

    public ListModel(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public ListModel(int pageNumber, int pageSize, List<T> datas) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.datas = datas;
    }
}
