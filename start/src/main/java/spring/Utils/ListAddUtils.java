package spring.Utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *  @Author xiao liang
 *  @Desprition 在链表中添加数据，添加时保证只有一个相同的实例
 */
public class ListAddUtils {

    public static <T> void add(List<T> list ,T t) {
        Set<T> set1 = new HashSet<>(list);
        if (set1.add(t)){
            list.add(t);
        }

    }

}
