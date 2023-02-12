package by.babanin.todo.view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import by.babanin.todo.view.about.AboutInfo;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.view.util.GUIUtils;

@Component
public final class UILauncher implements ApplicationListener<ContextRefreshedEvent> {

    private final BeanFactory beanFactory;
    private final ResourceBundleMessageSource messageSource;
    private final AboutInfo aboutInfo;
    private final FlatSVGIcon appLogoIcon;

    public UILauncher(BeanFactory beanFactory, ResourceBundleMessageSource messageSource, AboutInfo aboutInfo, FlatSVGIcon appLogoIcon) {
        this.beanFactory = beanFactory;
        this.messageSource = messageSource;
        this.aboutInfo = aboutInfo;
        this.appLogoIcon = appLogoIcon;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Translator.setMessageSource(messageSource);
        EventQueue.invokeLater(this::showMainFrame);
    }

    private void showMainFrame() {
        JFrame mainFrame = new JFrame();
        GUIUtils.setMainWindow(mainFrame);
        mainFrame.setJMenuBar(beanFactory.getBean(MainMenuBar.class));

        TodoPanel todoPanel = beanFactory.getBean(TodoPanel.class);
        todoPanel.load();
        mainFrame.setContentPane(todoPanel);

        mainFrame.setSize(GUIUtils.getLargeFrameSize());
        mainFrame.setMinimumSize(GUIUtils.getHalfFrameSize());
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setTitle(aboutInfo.getProduct().getName());
        mainFrame.setIconImage(appLogoIcon.getImage());
        mainFrame.setVisible(true);
    }
}
