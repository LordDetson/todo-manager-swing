package by.babanin.todo.view.component.table.adjustment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import by.babanin.todo.view.exception.ViewException;
import by.babanin.todo.view.settings.Setting;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableColumnAdjustment implements Setting {

    private boolean columnHeaderIncluded    = true;
    private boolean columnContentIncluded   = true;
    private boolean onlyAdjustLarger        = false;
    private boolean dynamicAdjustment       = true;

    @Override
    public void update(Setting setting) {
        TableColumnAdjustment tableColumnAdjustment = (TableColumnAdjustment) setting;
        this.columnHeaderIncluded = tableColumnAdjustment.columnHeaderIncluded;
        this.columnContentIncluded = tableColumnAdjustment.columnContentIncluded;
        this.onlyAdjustLarger = tableColumnAdjustment.onlyAdjustLarger;
        this.dynamicAdjustment = tableColumnAdjustment.dynamicAdjustment;
    }

    @Override
    public TableColumnAdjustment clone() {
        JsonMapper mapper = new JsonMapper();
        try {
            return mapper.readValue(mapper.writeValueAsString(this), TableColumnAdjustment.class);
        }
        catch(JsonProcessingException e) {
            throw new ViewException("Unexpected exception when cloning TableColumnAdjustment", e);
        }
    }
}
