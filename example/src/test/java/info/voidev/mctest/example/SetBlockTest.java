package info.voidev.mctest.example;

import info.voidev.mctest.api.MCTest;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import static org.assertj.core.api.Assertions.assertThat;

public class SetBlockTest {
    @MCTest
    public void change_block_material() {
        // Given:
        var block = Bukkit.getWorld("world").getBlockAt(0, 0, 0);

        // When:
        block.setType(Material.GREEN_WOOL);

        // Then:
        assertThat(block.getType()).isEqualTo(Material.GREEN_WOOL);
    }
}
