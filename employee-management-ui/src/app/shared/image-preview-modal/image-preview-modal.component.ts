import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-image-preview-modal',
  templateUrl: './image-preview-modal.component.html',
  styleUrl: './image-preview-modal.component.scss'
})
export class ImagePreviewModalComponent {
  readonly imageSrc = input.required<string>();
  readonly alt = input('Image preview');
  readonly close = output<void>();

  protected onClose(): void {
    this.close.emit();
  }
}
