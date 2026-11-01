package io.github.mortuusars.exposure.client.gui.screen.element;

import io.github.mortuusars.exposure.util.PagingDirection;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PagerTests {
    @Test
    void pageIndexIsClampedToLastAvailablePage() {
        Pager pager = new Pager().setPagesCount(3);

        pager.setPage(3);

        assertEquals(2, pager.getPage());
        assertFalse(pager.changePage(3));
        assertEquals(2, pager.getPage());
    }

    @Test
    void cycledNavigationNotifiesWithWrappedPage() {
        List<Integer> selectedPages = new ArrayList<>();
        Pager pager = new Pager()
                .setPagesCount(3)
                .setCycled(true)
                .setChangeCooldown(0)
                .onPageChanged((oldPage, newPage) -> selectedPages.add(newPage));

        assertTrue(pager.changePage(PagingDirection.PREVIOUS));
        assertEquals(2, pager.getPage());
        assertEquals(List.of(2), selectedPages);

        assertTrue(pager.changePage(PagingDirection.NEXT));
        assertEquals(0, pager.getPage());
        assertEquals(List.of(2, 0), selectedPages);
    }
}
