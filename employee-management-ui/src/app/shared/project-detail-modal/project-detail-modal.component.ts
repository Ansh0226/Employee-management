import { CommonModule } from '@angular/common';
import { Component, input, output } from '@angular/core';

import { ProjectDetailRecord } from '../../core/models';

@Component({
  selector: 'app-project-detail-modal',
  imports: [CommonModule],
  templateUrl: './project-detail-modal.component.html',
  styleUrl: './project-detail-modal.component.scss'
})
export class ProjectDetailModalComponent {
  readonly project = input.required<ProjectDetailRecord>();
  readonly close = output<void>();

  protected onClose(): void {
    this.close.emit();
  }
}
