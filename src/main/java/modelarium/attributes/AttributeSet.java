package modelarium.attributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributeSet {
    private static int attributeSetCount = 0;

    private final String name;
    private final List<Attribute> attributeList;
    private final Map<String, Integer> attributeIndexMap = new HashMap<String, Integer>();

    public AttributeSet(String name, List<Attribute> attributeList) {
        this.name = name;
        this.attributeList = attributeList;
        for (int i = 0; i < this.attributeList.size(); i++) {
            Attribute attribute = this.attributeList.get(i);
            this.attributeIndexMap.put(attribute.name(), i);
        }
        attributeSetCount++;
    }

    public AttributeSet(List<Attribute> attributeList) {
        this("attribute_set_" + attributeSetCount, attributeList);
    }

    public String name() {
        return name;
    }
}
