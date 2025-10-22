package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserTest {
    @Test
    void gettersSetters_andToString() {
        User u = new User();
        u.setId(1L);
        u.setName("Ivan");
        u.setEmail("ivan@test.com");

        assertThat(u.getId()).isEqualTo(1L);
        assertThat(u.getName()).isEqualTo("Ivan");
        assertThat(u.getEmail()).isEqualTo("ivan@test.com");
        assertThat(u.toString()).contains("Ivan").contains("ivan@test.com");
    }

    @Test
    void equalsHashCode_byId() {
        User a = new User(); a.setId(1L); a.setName("A"); a.setEmail("a@a");
        User b = new User(); b.setId(1L); b.setName("B"); b.setEmail("b@b");
        User c = new User(); c.setId(2L); c.setName("C"); c.setEmail("c@c");

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
        assertThat(a).isNotEqualTo(c);
        assertThat(a).isNotEqualTo(null);
        assertThat(a).isNotEqualTo(new Object());
        assertThat(a).isEqualTo(a); // рефлексивность
    }
}