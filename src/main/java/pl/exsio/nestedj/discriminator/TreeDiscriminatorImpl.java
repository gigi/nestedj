package pl.exsio.nestedj.discriminator;

import pl.exsio.nestedj.model.NestedNode;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeDiscriminatorImpl<N extends NestedNode<N>> implements TreeDiscriminator<N> {

    private Map<String, ValueProvider> valueProviders;

    public TreeDiscriminatorImpl(Map<String, ValueProvider> valueProviders) {
        this.valueProviders = valueProviders;
    }

    public TreeDiscriminatorImpl() {
        this.valueProviders = new HashMap<>();
    }

    public void setValueProviders(Map<String, ValueProvider> valueProviders) {
        this.valueProviders = valueProviders;
    }

    @Override
    public List<Predicate> getPredicates(CriteriaBuilder cb, Root<N> root) {
        List<Predicate> predicates = new ArrayList<>();
        for (Map.Entry<String, ValueProvider> providerEntry : valueProviders.entrySet()) {
            predicates.add(cb.equal(root.get(providerEntry.getKey()), providerEntry.getValue().getDiscriminatorValue()));
        }
        return predicates;
    }
}
