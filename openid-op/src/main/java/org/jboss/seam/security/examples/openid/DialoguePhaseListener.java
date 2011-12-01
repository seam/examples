package org.jboss.seam.security.examples.openid;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.ServletRequest;

import org.jboss.seam.security.external.dialogues.api.DialogueManager;
import org.jboss.seam.servlet.event.Initialized;
import org.jboss.seam.servlet.http.RequestParam;

public class DialoguePhaseListener {
    private static final long serialVersionUID = -3608798865478624561L;

    public final static String DIALOGUE_ID_PARAM = "dialogueId";

    @Inject
    private DialogueManager manager;

    @Inject
    @RequestParam("dialogueId")
    private String dialogueId;

    public void requestInitialized(@Observes @Initialized final ServletRequest request) {
        if (dialogueId != null && !manager.isAttached()) {
            manager.attachDialogue(dialogueId);
        }
    }
}
