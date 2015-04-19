package io.scaledml.ftrl.featuresprocessors;

import io.scaledml.core.SparseItem;
import io.scaledml.ftrl.util.LineBytesBuffer;
import io.scaledml.ftrl.util.Util;

public class SimpleFeaturesProcessor implements FeaturesProcessor {

    @Override
    public void addFeature(SparseItem item, LineBytesBuffer namespace, LineBytesBuffer feature, double value) {
        if (Util.doublesEqual(value, 0)) {
            return;
        }
        item.addIndex(Math.abs(Util.murmur().newHasher()
                .putBytes(namespace.bytes(), 0, namespace.size())
                .putBytes(feature.bytes(), 0, feature.size()).hash().asLong()), value);
    }

    @Override
    public void finalize(SparseItem item) {

    }
}
