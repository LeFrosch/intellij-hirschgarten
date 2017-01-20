/*
 * Copyright 2016 The Bazel Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.idea.blaze.base.actions;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.idea.blaze.base.model.primitives.Label;
import com.google.idea.blaze.base.targetmaps.SourceToTargetMap;
import com.google.idea.common.actionhelper.ActionPresentationHelper;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import java.io.File;

class BlazeCompileFileAction extends BlazeProjectAction {

  @Override
  protected void updateForBlazeProject(Project project, AnActionEvent e) {
    ActionPresentationHelper.of(e)
        .disableIf(getTargets(e).isEmpty())
        .setTextWithSubject("Compile File", "Compile %s", e.getData(CommonDataKeys.VIRTUAL_FILE))
        .disableWithoutSubject()
        .commit();
  }

  @Override
  protected void actionPerformedInBlazeProject(Project project, AnActionEvent e) {
    BlazeBuildService.getInstance().buildFile(project, getFileName(e), getTargets(e));
  }

  private ImmutableCollection<Label> getTargets(AnActionEvent e) {
    Project project = e.getProject();
    VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
    if (project != null && virtualFile != null) {
      return SourceToTargetMap.getInstance(project)
          .getTargetsToBuildForSourceFile(new File(virtualFile.getPath()));
    }
    return ImmutableList.of();
  }

  private static String getFileName(AnActionEvent e) {
    VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
    return virtualFile == null ? null : virtualFile.getName();
  }
}
