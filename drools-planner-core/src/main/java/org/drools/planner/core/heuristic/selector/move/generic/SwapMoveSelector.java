/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.planner.core.heuristic.selector.move.generic;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

import org.apache.commons.collections.IteratorUtils;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.common.iterator.AbstractOriginalSwapIterator;
import org.drools.planner.core.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.drools.planner.core.heuristic.selector.entity.EntitySelector;
import org.drools.planner.core.heuristic.selector.move.generic.chained.ChainedSwapMove;
import org.drools.planner.core.heuristic.selector.common.iterator.AbstractRandomSwapIterator;
import org.drools.planner.core.move.Move;

public class SwapMoveSelector extends GenericMoveSelector {

    protected final EntitySelector leftEntitySelector;
    protected final EntitySelector rightEntitySelector;
    protected final Collection<PlanningVariableDescriptor> variableDescriptors;
    protected final boolean randomSelection;

    protected final boolean leftEqualsRight;
    protected final boolean anyChained;

    public SwapMoveSelector(EntitySelector leftEntitySelector, EntitySelector rightEntitySelector,
            Collection<PlanningVariableDescriptor> variableDescriptors, boolean randomSelection) {
        this.leftEntitySelector = leftEntitySelector;
        this.rightEntitySelector = rightEntitySelector;
        this.variableDescriptors = variableDescriptors;
        this.randomSelection = randomSelection;
        leftEqualsRight = (leftEntitySelector == rightEntitySelector);
        PlanningEntityDescriptor leftEntityDescriptor = leftEntitySelector.getEntityDescriptor();
        PlanningEntityDescriptor rightEntityDescriptor = rightEntitySelector.getEntityDescriptor();
        if (!leftEntityDescriptor.getPlanningEntityClass().equals(rightEntityDescriptor.getPlanningEntityClass())) {
            throw new IllegalStateException("The moveSelector (" + this.getClass()
                    + ") has a leftEntitySelector's planningEntityClass ("
                    + leftEntityDescriptor.getPlanningEntityClass()
                    + ") which is not equal to the rightEntitySelector's planningEntityClass ("
                    + rightEntityDescriptor.getPlanningEntityClass() + ").");
        }
        boolean anyChained = false;
        for (PlanningVariableDescriptor variableDescriptor : variableDescriptors) {
            if (!leftEntityDescriptor.getPlanningEntityClass().equals(
                    variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass())) {
                throw new IllegalStateException("The moveSelector (" + this.getClass()
                        + ") has a variableDescriptor with a planningEntityClass ("
                        + variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass()
                        + ") which is not equal to the leftEntitySelector's planningEntityClass ("
                        + leftEntityDescriptor.getPlanningEntityClass() + ").");
            }
            if (variableDescriptor.isChained()) {
                anyChained = true;
            }
        }
        this.anyChained = anyChained;
        solverPhaseLifecycleSupport.addEventListener(leftEntitySelector);
        solverPhaseLifecycleSupport.addEventListener(rightEntitySelector);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isContinuous() {
        return leftEntitySelector.isContinuous() || rightEntitySelector.isContinuous();
    }

    public boolean isNeverEnding() {
        return randomSelection || leftEntitySelector.isNeverEnding() || rightEntitySelector.isNeverEnding();
    }

    public long getSize() {
        if (!leftEqualsRight) {
            return (long) leftEntitySelector.getSize() * (long) rightEntitySelector.getSize();
        } else {
            long leftSize = (long) leftEntitySelector.getSize();
            return leftSize * (leftSize - 1L) / 2L;
        }
    }

    public Iterator<Move> iterator() {
        if (!randomSelection) {
            return new OriginalSwapMoveIterator();
        } else {
            return new RandomSwapMoveIterator();
        }
    }

    private class OriginalSwapMoveIterator extends AbstractOriginalSwapIterator<Move, Object> {

        private OriginalSwapMoveIterator() {
            super(leftEntitySelector, rightEntitySelector);
        }

        @Override
        protected Move newSwapSelection(Object leftSubSelection, Object rightSubSelection) {
            return anyChained
                    ? new ChainedSwapMove(variableDescriptors, leftSubSelection, rightSubSelection)
                    : new SwapMove(variableDescriptors, leftSubSelection, rightSubSelection);
        }

    }

    private class RandomSwapMoveIterator extends AbstractRandomSwapIterator<Move, Object> {

        private RandomSwapMoveIterator() {
            super(leftEntitySelector, rightEntitySelector);
        }

        @Override
        protected Move newSwapSelection(Object leftSubSelection, Object rightSubSelection) {
            return anyChained
                    ? new ChainedSwapMove(variableDescriptors, leftSubSelection, rightSubSelection)
                    : new SwapMove(variableDescriptors, leftSubSelection, rightSubSelection);
        }

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + leftEntitySelector + ", " + rightEntitySelector + ")";
    }

}
