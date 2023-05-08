package by.babanin.todo.view.about;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.formdev.flatlaf.extras.components.FlatLabel;
import com.formdev.flatlaf.extras.components.FlatLabel.LabelType;

import by.babanin.ext.component.util.GUIUtils;
import by.babanin.ext.message.Translator;
import by.babanin.todo.image.IconResources;
import by.babanin.todo.view.about.AboutInfo.Contributor;
import by.babanin.todo.view.about.AboutInfo.Developer;
import by.babanin.todo.view.component.ImagePanel;
import by.babanin.todo.view.translat.AppTranslateCode;

public class AboutPanel extends JPanel {

    private static final int ICON_SIZE = 64;
    private static final int BORDER_SPACE = 20;
    private static final int PARAGRAPH_SPACE = 8;

    public AboutPanel(AboutInfo aboutInfo) {
        super(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.PAGE_START;
        c.insets = new Insets(BORDER_SPACE, BORDER_SPACE, BORDER_SPACE, BORDER_SPACE);
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 5;

        ImagePanel imagePanel = new ImagePanel(IconResources.getIcon("transparent_check_hexagon", ICON_SIZE).getImage());
        add(imagePanel, c);

        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(14, 0, 0, BORDER_SPACE);
        c.gridheight = 1;
        c.gridwidth = 3;
        c.gridx = 1;
        c.gridy = 0;

        FlatLabel productName = new FlatLabel();
        productName.setLabelType(LabelType.h2);
        productName.setText(aboutInfo.getProduct().getNameWithVersion());
        add(productName, c);

        c.insets = new Insets(PARAGRAPH_SPACE, 0, 0, BORDER_SPACE);
        c.gridy = 1;

        JLabel description = new JLabel(aboutInfo.getProduct().getDescription());
        add(description, c);

        c.insets = new Insets(0, 0, 0, BORDER_SPACE);
        c.gridy = 2;

        JLabel url = GUIUtils.createHyperlinkLabel(aboutInfo.getProduct().getUrl(), "Open-source project");
        add(url, c);

        c.gridy = 3;

        JLabel inceptionYear = new JLabel(aboutInfo.getCountry() + " " + aboutInfo.getProduct().getInceptionYear());
        add(inceptionYear, c);

        addAboutDevelopers(aboutInfo.getDevelopers(), c);

        addAboutContributors(aboutInfo.getContributors(), c);

        c.insets = new Insets(PARAGRAPH_SPACE, 0, BORDER_SPACE, BORDER_SPACE);
        c.gridy++;

        JLabel licenseName = GUIUtils.createHyperlinkLabel(aboutInfo.getLicense().getName());
        add(licenseName, c);
    }

    private GridBagConstraints addAboutDevelopers(List<Developer> developers, GridBagConstraints c) {
        c.insets = new Insets(PARAGRAPH_SPACE, 0, 0, BORDER_SPACE);
        c.gridy++;

        FlatLabel aboutDevelopers = new FlatLabel();
        aboutDevelopers.setLabelType(LabelType.h3);
        aboutDevelopers.setText(Translator.toLocale(AppTranslateCode.ABOUT_DEVELOPERS));
        add(aboutDevelopers, c);

        int keepedGridwidth = c.gridwidth;
        int keepedGridx = c.gridx;

        for(Developer developer : developers) {
            c.insets = new Insets(PARAGRAPH_SPACE, 0, 0, BORDER_SPACE);
            c.gridy++;

            JLabel name = new JLabel(developer.getName());
            add(name, c);

            c.insets = new Insets(0, 0, 0, BORDER_SPACE);
            c.gridwidth = 1;
            c.gridy++;

            JLabel emailLabel = new JLabel("Email:");
            add(emailLabel, c);

            c.gridx++;

            JLabel email = GUIUtils.createHyperlinkLabel(developer.getEmail());
            add(email, c);

            c.gridx--;
            c.gridy++;

            JLabel githubLabel = new JLabel("GitHub:");
            add(githubLabel, c);

            c.gridx++;

            JLabel github = GUIUtils.createHyperlinkLabel(developer.getGithub());
            add(github, c);

            c.gridx--;
            c.gridy++;

            JLabel linkedinLabel = new JLabel("LinkedIn:");
            add(linkedinLabel, c);

            c.gridx++;

            JLabel linkedin = GUIUtils.createHyperlinkLabel(developer.getLinkedin());
            add(linkedin, c);

            c.insets = new Insets(0, 0, 0, BORDER_SPACE);
            c.gridx--;
            c.gridy++;

            JLabel telegramLabel = new JLabel("Telegram:");
            add(telegramLabel, c);

            c.gridx++;

            JLabel telegram = GUIUtils.createHyperlinkLabel(developer.getTelegram());
            add(telegram, c);

            c.gridx = keepedGridx;
        }

        c.gridwidth = keepedGridwidth;

        return c;
    }

    private GridBagConstraints addAboutContributors(List<Contributor> contributors, GridBagConstraints c) {
        c.insets = new Insets(PARAGRAPH_SPACE, 0, 0, BORDER_SPACE);
        c.gridy++;

        FlatLabel aboutContributors = new FlatLabel();
        aboutContributors.setLabelType(LabelType.h3);
        aboutContributors.setText(Translator.toLocale(AppTranslateCode.ABOUT_CONTRIBUTORS));
        add(aboutContributors, c);

        int keepedGridwidth = c.gridwidth;
        int keepedGridx = c.gridx;

        for(Contributor contributor : contributors) {
            c.insets = new Insets(PARAGRAPH_SPACE, 0, 0, BORDER_SPACE);
            c.gridy++;

            JLabel name = new JLabel(contributor.getName());
            add(name, c);

            c.insets = new Insets(0, 0, 0, BORDER_SPACE);
            c.gridwidth = 1;
            c.gridy++;

            JLabel emailLabel = new JLabel("Email:");
            add(emailLabel, c);

            c.gridx++;

            JLabel email = GUIUtils.createHyperlinkLabel(contributor.getEmail());
            add(email, c);

            c.gridx--;
            c.gridy++;

            JLabel linkedinLabel = new JLabel("LinkedIn:");
            add(linkedinLabel, c);

            c.gridx++;

            JLabel linkedin = GUIUtils.createHyperlinkLabel(contributor.getLinkedin());
            add(linkedin, c);

            c.insets = new Insets(0, 0, 0, BORDER_SPACE);
            c.gridx--;
            c.gridy++;

            JLabel telegramLabel = new JLabel("Telegram:");
            add(telegramLabel, c);

            c.gridx++;

            JLabel telegram = GUIUtils.createHyperlinkLabel(contributor.getTelegram());
            add(telegram, c);

            c.gridx = keepedGridx;
        }

        c.gridwidth = keepedGridwidth;

        return c;
    }
}
