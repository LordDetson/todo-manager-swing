package by.babanin.todo.view.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;

import by.babanin.todo.view.component.table.adjustment.TableColumnAdjustment;
import lombok.Getter;

@Getter
@ConfigurationProperties("settings")
public class Settings {

    private final TableColumnAdjustment tableColumnAdjustment = new TableColumnAdjustment();
}
