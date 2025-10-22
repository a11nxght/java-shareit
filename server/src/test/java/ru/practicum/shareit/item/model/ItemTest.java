package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ItemTest {
    @Test
    void gettersSetters_andToString() {
        User owner = new User(); owner.setId(10L);
        Item i = new Item();
        i.setId(5L);
        i.setName("дрель");
        i.setDescription("мощная");
        i.setAvailable(true);
        i.setOwner(owner);
        i.setRequest(null);

        assertThat(i.getId()).isEqualTo(5L);
        assertThat(i.getName()).isEqualTo("дрель");
        assertThat(i.getDescription()).isEqualTo("мощная");
        assertThat(i.isAvailable()).isTrue();
        assertThat(i.getOwner()).isSameAs(owner);
        assertThat(i.getRequest()).isNull();
        assertThat(i.toString()).contains("дрель").contains("мощная");
    }

    @Test
    void equalsHashCode_byId_andNotEqualDifferentIdOrType() {
        Item a = new Item(); a.setId(1L);
        Item b = new Item(); b.setId(1L);
        Item c = new Item(); c.setId(2L);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
        assertThat(a).isNotEqualTo(c);
        assertThat(a).isNotEqualTo(null);
        assertThat(a).isNotEqualTo(new Object());
    }
}